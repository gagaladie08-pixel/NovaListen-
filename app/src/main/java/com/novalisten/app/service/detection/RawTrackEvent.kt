// service/detection/RawTrackEvent.kt
package com.novalisten.app.service.detection

import com.novalisten.app.domain.model.entity.DetectionMethod
import com.novalisten.app.domain.model.entity.PlaySource

/**
 * État de lecture détecté.
 */
enum class PlaybackState {
    PLAYING,
    PAUSED,
    STOPPED,
    UNKNOWN
}

/**
 * Événement brut détecté par MediaSession ou NotificationListener.
 * Non validé — passera par le pipeline de validation.
 *
 * C'est la "matière première" de la détection.
 */
data class RawTrackEvent(
    val title: String,
    val artist: String,
    val album: String?,
    val durationMs: Long,
    val positionMs: Long,
    val state: PlaybackState,
    val source: PlaySource,
    val detectionMethod: DetectionMethod,
    val sourceApp: String,           // Package de l'app source (ex: com.spotify.music)
    val detectedAt: Long = System.currentTimeMillis()
) {
    /** Clé unique pour identifier un morceau (sans doublon) */
    fun uniqueKey(): String = "${title.trim().lowercase()}_${artist.trim().lowercase()}"
}