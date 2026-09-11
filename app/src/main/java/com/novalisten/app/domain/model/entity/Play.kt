// domain/model/entity/Play.kt
package com.novalisten.app.domain.model.entity

/**
 * Source de lecture détectée.
 */
enum class PlaySource {
    SPOTIFY, YOUTUBE_MUSIC, DEEZER, TIDAL,
    APPLE_MUSIC, VLC, LOCAL, OTHER
}

/**
 * Méthode de détection utilisée.
 */
enum class DetectionMethod {
    MEDIASESSION, NOTIFICATION, MANUAL
}

/**
 * Type d'appareil.
 */
enum class DeviceType {
    PHONE, TABLET, CAR, OTHER
}

/**
 * Modèle métier d'une écoute — LE CŒUR de NovaListen.
 *
 * Règles métier :
 * - isValid = true si durationListened >= 30 000ms
 * - completionRate = durationListened / track.durationMs
 * - sessionId regroupe les écoutes d'une même session (gap < 15 min)
 *
 * Départage en cas d'égalité :
 * - Tri principal  : playCount
 * - Tri secondaire : listenTimeMs
 */
data class Play(
    val id: Long,
    val trackId: Long,
    val artistId: Long,
    val albumId: Long?,
    val startedAt: Long,
    val endedAt: Long?,
    val durationListened: Long,
    val completionRate: Float,
    val source: PlaySource,
    val detectionMethod: DetectionMethod,
    val isValid: Boolean,
    val sessionId: String,
    val deviceType: DeviceType
)