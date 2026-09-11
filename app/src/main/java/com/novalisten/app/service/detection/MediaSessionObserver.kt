// service/detection/MediaSessionObserver.kt
package com.novalisten.app.service.detection

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import com.novalisten.app.domain.model.entity.DetectionMethod
import com.novalisten.app.domain.model.entity.PlaySource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observe les sessions MediaSession actives sur le système.
 *
 * Source prioritaire (Priorité 1) car les données sont
 * plus riches et plus fiables que les notifications.
 *
 * Nécessite la permission BIND_NOTIFICATION_LISTENER_SERVICE
 * pour accéder aux sessions via getActiveSessions().
 */
@Singleton
class MediaSessionObserver @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /** Callback appelé quand un nouvel événement est détecté */
    var onTrackEvent: ((RawTrackEvent) -> Unit)? = null

    private val sessionManager by lazy {
        context.getSystemService(Context.MEDIA_SESSION_SERVICE)
                as MediaSessionManager
    }

    private val activeCallbacks =
        mutableMapOf<MediaController, MediaController.Callback>()

    /**
     * Démarre l'observation des sessions MediaSession.
     * Appelé par DetectionService.onCreate().
     *
     * @param notificationListenerToken Token du NotificationListenerService
     */
    fun start(notificationListenerToken: android.content.ComponentName) {
        try {
            val controllers = sessionManager.getActiveSessions(
                notificationListenerToken
            )
            controllers.forEach { registerController(it) }

            // Observer les nouvelles sessions
            sessionManager.addOnActiveSessionsChangedListener(
                { newControllers ->
                    // Désenregistre les anciens
                    activeCallbacks.keys.toList().forEach { unregisterController(it) }
                    // Enregistre les nouveaux
                    newControllers?.forEach { registerController(it) }
                },
                notificationListenerToken
            )
        } catch (e: SecurityException) {
            // Permission non accordée → fallback sur NotificationListener
        }
    }

    /**
     * Arrête l'observation.
     */
    fun stop() {
        activeCallbacks.keys.toList().forEach { unregisterController(it) }
        activeCallbacks.clear()
    }

    // ── Privé ─────────────────────────────────────────────────────────────

    private fun registerController(controller: MediaController) {
        val callback = object : MediaController.Callback() {
            override fun onMetadataChanged(metadata: MediaMetadata?) {
                handleMediaUpdate(controller, metadata)
            }

            override fun onPlaybackStateChanged(state: PlaybackState?) {
                handleMediaUpdate(controller, controller.metadata, state)
            }
        }
        controller.registerCallback(callback)
        activeCallbacks[controller] = callback
    }

    private fun unregisterController(controller: MediaController) {
        activeCallbacks[controller]?.let { controller.unregisterCallback(it) }
        activeCallbacks.remove(controller)
    }

    private fun handleMediaUpdate(
        controller: MediaController,
        metadata: MediaMetadata?,
        state: PlaybackState? = controller.playbackState
    ) {
        val title  = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
            ?: return
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
            ?: metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
            ?: return
        val album    = metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
        val position = state?.position ?: 0L

        val playbackState = when (state?.state) {
            PlaybackState.STATE_PLAYING -> PlaybackState_PLAYING
            PlaybackState.STATE_PAUSED  -> PlaybackState_PAUSED
            PlaybackState.STATE_STOPPED -> PlaybackState_STOPPED
            else                        -> PlaybackState_UNKNOWN
        }

        val source = detectSource(controller.packageName)

        onTrackEvent?.invoke(
            RawTrackEvent(
                title           = title,
                artist          = artist,
                album           = album,
                durationMs      = duration,
                positionMs      = position,
                state           = playbackState,
                source          = source,
                detectionMethod = DetectionMethod.MEDIASESSION,
                sourceApp       = controller.packageName
            )
        )
    }

    private fun detectSource(packageName: String): PlaySource =
        when (packageName) {
            "com.spotify.music"                          -> PlaySource.SPOTIFY
            "com.google.android.apps.youtube.music"      -> PlaySource.YOUTUBE_MUSIC
            "deezer.android.app"                         -> PlaySource.DEEZER
            "com.tidal.wave"                             -> PlaySource.TIDAL
            "com.apple.android.music"                    -> PlaySource.APPLE_MUSIC
            "org.videolan.vlc"                           -> PlaySource.VLC
            else                                         -> PlaySource.OTHER
        }

    // Aliases pour éviter la confusion avec android.media.session.PlaybackState
    private val PlaybackState_PLAYING = com.novalisten.app.service.detection.PlaybackState.PLAYING
    private val PlaybackState_PAUSED  = com.novalisten.app.service.detection.PlaybackState.PAUSED
    private val PlaybackState_STOPPED = com.novalisten.app.service.detection.PlaybackState.STOPPED
    private val PlaybackState_UNKNOWN = com.novalisten.app.service.detection.PlaybackState.UNKNOWN
}