// domain/usecase/billboard/ComputeBillboardSnapshotUseCase.kt
package com.novalisten.app.domain.usecase.billboard

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.BillboardEntry
import com.novalisten.app.domain.model.entity.BillboardSnapshot
import com.novalisten.app.domain.model.enums.ChartType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Movement
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IBillboardRepository
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.usecase.stats.PeriodKeyHelper.toPeriodKey
import javax.inject.Inject

/**
 * Calcule et sauvegarde un snapshot Billboard.
 *
 * Appelé à la fin de chaque période calendaire :
 * → Fin de journée  → snapshot DAILY
 * → Fin de semaine  → snapshot WEEKLY
 * → Fin de mois     → snapshot MONTHLY
 * → Fin d'année     → snapshot YEARLY
 *
 * Limites par chart et période :
 * NOVA_HOT_100   : Daily=75,  sinon 100
 * NOVA_ARTIST_50 : Daily=25,  sinon 50
 * NOVA_75_ALBUMS : Daily=50,  sinon 75
 *
 * Mouvements :
 * Daily   → vs hier
 * Weekly  → vs semaine précédente
 * Monthly → vs mois précédent
 * Yearly  → vs année précédente
 * Global  → vs semaine précédente
 */
class ComputeBillboardSnapshotUseCase @Inject constructor(
    private val playRepository: IPlayRepository,
    private val billboardRepository: IBillboardRepository
) {

    suspend operator fun invoke(
        chartType: ChartType,
        period: Period
    ): Result<BillboardSnapshot> {
        return try {
            val periodKey  = period.toPeriodKey()
            val limit      = chartType.limitForPeriod(period)
            val entityType = chartType.toEntityType()

            // 1. Récupère le top pour cette période
            val top = playRepository.getTopEntities(
                entityType = entityType,
                period     = period,
                periodKey  = periodKey,
                limit      = limit
            )

            // 2. Récupère le snapshot précédent pour calculer les mouvements
            val prevPeriodKey = getPreviousPeriodKey(period, periodKey)
            val prevSnapshot  = prevPeriodKey?.let {
                billboardRepository.getSnapshot(chartType, period, it)
            }

            // 3. Construit les entrées avec mouvements
            val entries = top.mapIndexed { index, (entityId, playCount) ->
                val currentRank  = index + 1
                val previousRank = prevSnapshot?.entries
                    ?.find { it.entityId == entityId }?.rank

                val movement = computeMovement(currentRank, previousRank)

                val peakRank = minOf(
                    currentRank,
                    billboardRepository.getPeakRank(entityId, chartType) ?: currentRank
                )

                val weeksOnChart = billboardRepository.getWeeksOnChart(
                    entityId, chartType
                ) + 1

                BillboardEntry(
                    id              = 0,
                    entityType      = entityType,
                    entityId        = entityId,
                    rank            = currentRank,
                    previousRank    = previousRank,
                    peakRank        = peakRank,
                    weeksOnChart    = weeksOnChart,
                    movement        = movement,
                    movementValue   = computeMovementValue(
                        currentRank, previousRank
                    ),
                    playCount       = playCount,
                    listenTimeMs    = 0L,
                    hofDirectDebut  = isDirectDebut(
                        movement, currentRank, period
                    ),
                    hofLongRun      = false, // Calculé par EvaluateHallOfFameUseCase
                    hofTripleDebut  = false,
                    hofLegendaryRun = false,
                    hofEntryDate    = null
                )
            }

            // 4. Crée et sauvegarde le snapshot
            val snapshot = BillboardSnapshot(
                chartType    = chartType,
                period       = period,
                periodKey    = periodKey,
                calculatedAt = System.currentTimeMillis(),
                entries      = entries
            )

            billboardRepository.saveSnapshot(snapshot)
            Result.Success(snapshot)

        } catch (e: Exception) {
            Result.Error("Erreur calcul Billboard : ${e.message}", e)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private fun ChartType.toEntityType(): EntityType = when (this) {
        ChartType.NOVA_HOT_100   -> EntityType.SONG
        ChartType.NOVA_ARTIST_50 -> EntityType.ARTIST
        ChartType.NOVA_75_ALBUMS -> EntityType.ALBUM
    }

    private fun computeMovement(
        current: Int,
        previous: Int?
    ): Movement = when {
        previous == null -> Movement.NEW
        current < previous -> Movement.UP
        current > previous -> Movement.DOWN
        else               -> Movement.STABLE
    }

    private fun computeMovementValue(
        current: Int,
        previous: Int?
    ): Int = when {
        previous == null -> 0
        else             -> kotlin.math.abs(previous - current)
    }

    private fun isDirectDebut(
        movement: Movement,
        rank: Int,
        period: Period
    ): Boolean = movement == Movement.NEW && rank == 1 &&
            (period == Period.WEEKLY || period == Period.MONTHLY)

    /**
     * Clé de la période précédente pour calculer les mouvements.
     */
    private fun getPreviousPeriodKey(
        period: Period,
        currentKey: String
    ): String? {
        val now = java.time.LocalDate.now()
        return when (period) {
            Period.DAILY   -> now.minusDays(1).toString()
            Period.WEEKLY  -> {
                val prev  = now.minusWeeks(1)
                val week  = prev.get(
                    java.time.temporal.WeekFields.ISO.weekOfYear()
                )
                "${prev.year}-W${week.toString().padStart(2, '0')}"
            }
            Period.MONTHLY -> {
                val prev = now.minusMonths(1)
                "${prev.year}-${prev.monthValue.toString().padStart(2, '0')}"
            }
            Period.YEARLY  -> (now.year - 1).toString()
            Period.GLOBAL  -> null
        }
    }
}