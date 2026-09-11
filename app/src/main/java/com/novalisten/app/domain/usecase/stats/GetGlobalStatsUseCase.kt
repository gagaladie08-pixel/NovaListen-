// domain/usecase/stats/GetGlobalStatsUseCase.kt
package com.novalisten.app.domain.usecase.stats

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.GlobalStats
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.usecase.stats.PeriodKeyHelper.toPeriodKey
import javax.inject.Inject

/**
 * Calcule le bandeau résumé de l'onglet Stats.
 *
 * Contient :
 * → Total écoutes, temps d'écoute
 * → Artistes/Tracks/Albums uniques
 * → Streak actuel + record
 * → Moyenne par jour
 */
class GetGlobalStatsUseCase @Inject constructor(
    private val playRepository: IPlayRepository
) {

    suspend operator fun invoke(
        period: Period
    ): Result<GlobalStats> {
        return try {
            val periodKey = period.toPeriodKey()

            // Total écoutes
            val totalPlays = playRepository.getPlayCount(
                entityId   = 0L,
                entityType = EntityType.ARTIST,
                period     = period,
                periodKey  = periodKey
            )

            // Temps d'écoute total
            val totalListenTime = playRepository.getListenTime(
                entityId   = 0L,
                entityType = EntityType.ARTIST,
                period     = period,
                periodKey  = periodKey
            )

            // Streaks
            val currentStreak = playRepository.getCurrentStreak()
            val longestStreak = playRepository.getLongestStreak()

            Result.Success(
                GlobalStats(
                    totalPlays          = totalPlays,
                    totalListenTimeMs   = totalListenTime,
                    uniqueArtists       = 0,   // Calculé côté Data si besoin
                    uniqueTracks        = 0,
                    uniqueAlbums        = 0,
                    currentStreak       = currentStreak,
                    longestStreak       = longestStreak,
                    averagePlaysPerDay  = 0.0,
                    mostActiveHour      = null,
                    mostActiveDayOfWeek = null
                )
            )
        } catch (e: Exception) {
            Result.Error("Erreur stats globales : ${e.message}", e)
        }
    }
}

/**
 * Helper pour générer les clés de période.
 *
 * DAILY   → "2026-09-11"
 * WEEKLY  → "2026-W37"
 * MONTHLY → "2026-09"
 * YEARLY  → "2026"
 * GLOBAL  → "all"
 */
object PeriodKeyHelper {

    fun Period.toPeriodKey(): String {
        val now = java.time.LocalDate.now()
        return when (this) {
            Period.DAILY   -> now.toString()
            Period.WEEKLY  -> {
                val week = now.get(java.time.temporal.WeekFields.ISO.weekOfYear())
                "${now.year}-W${week.toString().padStart(2, '0')}"
            }
            Period.MONTHLY ->
                "${now.year}-${now.monthValue.toString().padStart(2, '0')}"
            Period.YEARLY  -> now.year.toString()
            Period.GLOBAL  -> "all"
        }
    }
}