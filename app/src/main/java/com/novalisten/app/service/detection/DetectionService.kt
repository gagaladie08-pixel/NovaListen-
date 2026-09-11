// service/detection/DetectionService.kt
package com.novalisten.app.service.detection

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.novalisten.app.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Service de détection musicale en arrière-plan.
 *
 * ForegroundService obligatoire sur Android 8+.
 * Affiche une notification persistante (exigence Android).
 *
 * Pipeline de détection :
 * RawTrackEvent
 *   → TrackNormalizer
 *   → TrackDeduplicator
 *   → (Timer 30s)
 *   → TrackValidator
 *   → PlaySaver → BDD
 */
@AndroidEntryPoint
class DetectionService : Service() {

    @Inject lateinit var normalizer: TrackNormalizer
    @Inject lateinit var deduplicator: TrackDeduplicator
    @Inject lateinit var validator: TrackValidator
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var playSaver: PlaySaver
    @Inject lateinit var mediaSessionObserver: MediaSessionObserver
    @Inject lateinit var notificationObserver: NotificationObserver

    companion object {
        const val NOTIFICATION_ID      = 1001
        const val CHANNEL_ID           = "novalisten_detection"
        const val CHANNEL_NAME         = "Détection musicale"

        /** Durée minimale avant validation : 30s */
        const val VALIDATION_DELAY_MS  = 30_000L

        fun start(context: Context) {
            val intent = Intent(context, DetectionService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, DetectionService::class.java)
            context.stopService(intent)
        }
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Écoute en cours (en attente de validation) */
    private var currentEvent: RawTrackEvent? = null
    private var listenStartedAt: Long        = 0L
    private var validationJob: Job?          = null

    // ── Lifecycle ─────────────────────────────────────────────────────────

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        setupObservers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // Se relance automatiquement si tué
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSessionObserver.stop()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // ── Setup ─────────────────────────────────────────────────────────────

    private fun setupObservers() {
        // Les 2 observateurs pointent vers le même handler
        mediaSessionObserver.onTrackEvent  = ::handleRawEvent
        notificationObserver.onTrackEvent  = ::handleRawEventFromNotification
    }

    // ── Pipeline de détection ─────────────────────────────────────────────

    /**
     * Handler principal — MediaSession (Priorité 1)
     */
    private fun handleRawEvent(event: RawTrackEvent) {
        serviceScope.launch {
            processEvent(event)
        }
    }

    /**
     * Handler fallback — Notification (Priorité 2)
     * Ignoré si MediaSession a déjà détecté quelque chose récemment.
     */
    private fun handleRawEventFromNotification(event: RawTrackEvent) {
        // Si MediaSession est actif sur cet app → on ignore le fallback
        if (currentEvent?.sourceApp == event.sourceApp &&
            currentEvent?.detectionMethod ==
            com.novalisten.app.domain.model.entity.DetectionMethod.MEDIASESSION) {
            return
        }
        serviceScope.launch { processEvent(event) }
    }

    private suspend fun processEvent(raw: RawTrackEvent) {

        // ÉTAPE 1 — Normalisation
        val event = normalizer.normalize(raw)

        // ÉTAPE 2 — État STOPPED → annule l'écoute en cours
        if (event.state == PlaybackState.STOPPED) {
            handleStop()
            return
        }

        // ÉTAPE 3 — État PAUSED → pause (on garde l'écoute en cours)
        if (event.state == PlaybackState.PAUSED) {
            return
        }

        // ÉTAPE 4 — Déduplication
        if (deduplicator.isDuplicate(event)) return

        // ÉTAPE 5 — Nouveau morceau détecté
        handleNewTrack(event)
    }

    private suspend fun handleNewTrack(event: RawTrackEvent) {
        // Annule le timer précédent si un morceau était en cours
        validationJob?.cancel()

        // Sauvegarde l'écoute précédente si elle a dépassé 30s
        currentEvent?.let { prev ->
            val duration = System.currentTimeMillis() - listenStartedAt
            if (duration >= VALIDATION_DELAY_MS) {
                val result = validator.validate(duration, prev)
                if (result is ValidationResult.Valid) {
                    playSaver.save(prev, duration, listenStartedAt)
                }
            }
        }

        // Démarre le suivi du nouveau morceau
        currentEvent    = event
        listenStartedAt = System.currentTimeMillis()

        // Lance le timer de validation (30s)
        validationJob = serviceScope.launch {
            kotlinx.coroutines.delay(VALIDATION_DELAY_MS)
            // Après 30s → le morceau est pré-validé (sera finalisé à la fin)
        }

        // Met à jour la notification
        updateNotification(event)
    }

    private fun handleStop() {
        validationJob?.cancel()
        currentEvent?.let { event ->
            val duration = System.currentTimeMillis() - listenStartedAt
            serviceScope.launch {
                val result = validator.validate(duration, event)
                if (result is ValidationResult.Valid) {
                    playSaver.save(event, duration, listenStartedAt)
                }
            }
        }
        currentEvent    = null
        listenStartedAt = 0L
        updateNotification(null)
    }

    // ── Notification persistante ───────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW   // Silencieuse
        ).apply {
            description        = "NovaListen détecte ta musique en arrière-plan"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(event: RawTrackEvent? = null): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val contentText = if (event != null) {
            "🎵 ${event.title} — ${event.artist}"
        } else {
            "En attente de musique…"
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("NovaListen actif")
            .setContentText(contentText)
            .setContentIntent(pendingIntent)
            .setOngoing(true)           // Non dismissable
            .setSilent(true)            // Silencieuse
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(event: RawTrackEvent?) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(event))
    }
}