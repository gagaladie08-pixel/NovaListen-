// domain/model/entity/GlobalStats.kt
package com.novalisten.app.domain.model.entity

/**
 * Bandeau résumé affiché en haut de l'onglet Stats.
 * Toutes les métriques globales pour une période donnée.
 */
data class GlobalStats(
    val totalPlays: Int,
    val totalListenTimeMs: Long,
    val uniqueArtists: Int,
    val uniqueTracks: Int,
    val uniqueAlbums: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val averagePlaysPerDay: Double,
    val mostActiveHour: Int?,       // 0-23
    val mostActiveDayOfWeek: Int?   // 1=Lundi, 7=Dimanche
) {
    /** Temps total formaté */
    fun totalListenTimeFormatted(): String {
        val days    = totalListenTimeMs / 86_400_000
        val hours   = (totalListenTimeMs % 86_400_000) / 3_600_000
        val minutes = (totalListenTimeMs % 3_600_000) / 60_000
        return when {
            days > 0    -> "${days}j ${hours}h"
            hours > 0   -> "${hours}h ${minutes}min"
            minutes > 0 -> "${minutes}min"
            else        -> "${totalListenTimeMs / 1_000}s"
        }
    }
}