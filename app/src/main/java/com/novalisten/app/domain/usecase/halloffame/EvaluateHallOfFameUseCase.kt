// domain/usecase/halloffame/EvaluateHallOfFameUseCase.kt
package com.novalisten.app.domain.usecase.halloffame

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.BillboardSnapshot
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IBillboardRepository
import javax.inject.Inject

/**
 * Évalue les entrées au Hall of Fame après chaque snapshot Billboard.
 *
 * Règles d'entrée (automatiques, jamais manuelles) :
 *
 * 🚀 DIRECT DEBUT
 *    Entrée directe #1 Weekly ou Monthly
 *
 * 👑 LONG RUN
 *    3 semaines consécutives #1 Weekly
 *    OU 2 mois consécutifs #1 Monthly
 *
 * 🌍 TRIPLE DEBUT (Global)
 *    #1 Daily + Weekly + Monthly simultanément
 *
 * 🏅 LEGENDARY RUN (Global)
 *    10× #1 Weekly non consécutives
 *
 * Une entité peut avoir plusieurs badges (1 carte avec tous).
 */
class EvaluateHallOfFameUseCase @Inject constructor(
    private val billboardRepository: IBillboardRepository
) {

    suspend operator fun invoke(
        snapshot: BillboardSnapshot
    ): Result<Unit> {
        return try {
            snapshot.entries.forEach { entry ->
                evaluateEntry(entry.entityId, snapshot)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Erreur Hall of Fame : ${e.message}", e)
        }
    }

    private suspend fun evaluateEntry(
        entityId: Long,
        snapshot: BillboardSnapshot
    ) {
        val entry = snapshot.entries.find { it.entityId == entityId }
            ?: return

        // 🚀 DIRECT DEBUT
        val isDirectDebut = entry.rank == 1 &&
                entry.previousRank == null &&
                (snapshot.period == Period.WEEKLY ||
                        snapshot.period == Period.MONTHLY)

        // 👑 LONG RUN — 3 semaines consécutives #1
        val weeksAt1 = billboardRepository.getWeeksOnChart(
            entityId, snapshot.chartType
        )
        val isLongRun = when (snapshot.period) {
            Period.WEEKLY  -> entry.rank == 1 && weeksAt1 >= 3
            Period.MONTHLY -> entry.rank == 1 && weeksAt1 >= 2
            else           -> false
        }

        // 🏅 LEGENDARY RUN — 10× #1 Weekly
        val totalWeeksAt1 = if (snapshot.period == Period.WEEKLY) {
            billboardRepository.getWeeksOnChart(entityId, snapshot.chartType)
        } else 0
        val isLegendaryRun = totalWeeksAt1 >= 10

        // Met à jour le snapshot si nécessaire
        if (isDirectDebut || isLongRun || isLegendaryRun) {
            val updated = snapshot.entries.map { e ->
                if (e.entityId == entityId) {
                    e.copy(
                        hofDirectDebut  = e.hofDirectDebut  || isDirectDebut,
                        hofLongRun      = e.hofLongRun      || isLongRun,
                        hofLegendaryRun = e.hofLegendaryRun || isLegendaryRun,
                        hofEntryDate    = e.hofEntryDate
                            ?: System.currentTimeMillis()
                    )
                } else e
            }
            // Sauvegarde le snapshot mis à jour
            billboardRepository.saveSnapshot(snapshot.copy(entries = updated))
        }
    }
}