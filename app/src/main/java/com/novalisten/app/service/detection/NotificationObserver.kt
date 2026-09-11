// service/detection/NotificationObserver.kt
package com.novalisten.app.service.detection

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.novalisten.app.domain.model.entity.DetectionMethod
import com.novalisten.app.domain.model.entity.PlaySource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Analyse les notifications musicales.
 *
 * Source de fallback (Priorité 2) utilisée quand
 * MediaSession n'est pas disponible pour une app.
 *
 * Fonctionne pour :
 * ✅ Spotify, YouTube Music, Deezer, Tidal
 * ✅ Apple Music, VLC, SoundCloud
 * ⚠️ YouTube (pas toujours fiable)
 */
@Singleton
class NotificationObserver @Inject constructor() {

    companion object {
        /** Apps musicales dont on analyse les notifications */
        private val MUSIC_APP_PACKAGES = setOf(
            "com.spotify.music",
            "com.google.android.apps.youtube.music",
            "deezer.android.app",
            "com.tidal.wave",
            "com.apple.android.music",
            "org.videolan.vlc",
            "com.soundcloud.android"
        )
    }

    /** Callback appelé quand un événement est extrait */
    var onTrackEvent: ((RawTrackEvent) -> Unit)? = null

    /**
     * Analyse une notification entrant du NotificationListener.
     * Appelé par NovaListenNotificationListener.onNotificationPosted()
     */
    fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName

        // On ignore les apps non musicales
        if (pkg !in MUSIC_APP_PACKAGES) return

        val notification = sbn.notification ?: return
        val extras       = notification.extras ?: return

        // Extraction des métadonnées depuis les extras de notification
        val title  = extras.getString(Notification.EXTRA_TITLE)?.takeIf { it.isNotBlank() }
            ?: return
        val artist = extras.getString(Notification.EXTRA_TEXT)?.takeIf { it.isNotBlank() }
            ?: extras.getString(Notification.EXTRA_SUB_TEXT)?.takeIf { it.isNotBlank() }
            ?: return

        val source = detectSource(pkg)

        onTrackEvent?.invoke(
            RawTrackEvent(
                title           = title,
                artist          = artist,
                album           = null,
                durationMs      = 0L,  // Non disponible via notification
                positionMs      = 0L,
                state           = PlaybackState.PLAYING, // Notif active = lecture
                source          = source,
                detectionMethod = DetectionMethod.NOTIFICATION,
                sourceApp       = pkg
            )
        )
    }

    /**
     * Notification supprimée = lecture stoppée.
     */
    fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName !in MUSIC_APP_PACKAGES) return
        // Géré par le pipeline via l'état STOPPED
    }

    private fun detectSource(packageName: String): PlaySource =
        when (packageName) {
            "com.spotify.music"                     -> PlaySource.SPOTIFY
            "com.google.android.apps.youtube.music" -> PlaySource.YOUTUBE_MUSIC
            "deezer.android.app"                    -> PlaySource.DEEZER
            "com.tidal.wave"                        -> PlaySource.TIDAL
            "com.apple.android.music"               -> PlaySource.APPLE_MUSIC
            "org.videolan.vlc"                      -> PlaySource.VLC
            else                                    -> PlaySource.OTHER
        }
}