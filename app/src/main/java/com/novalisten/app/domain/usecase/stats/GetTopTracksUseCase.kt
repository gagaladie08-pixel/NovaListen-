// domain/usecase/stats/GetTopTracksUseCase.kt
package com.novalisten.app.domain.usecase.stats

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.RankedItem
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.usecase.stats.PeriodKeyHelper.toPeriodKey
import javax.inject.Inject

/**
 * Retourne le Top 300 morceaux pour une période donnée.
 *
 * Tri :
 * → Principal  : playCount DESC
 * → Secondaire : listenTimeMs DESC (départage égalités)
 */
class GetTopTracksUseCase @Inject constructor(
    private val playRepository: IPlayRepository
) {

    /**
     * @param period    La période (DAILY/WEEKLY/MONTHLY/YEARLY/GLOBAL)
     * @param limit     Nombre maximum d'entrées (défaut: 300)
     */
    suspend operator fun invoke(
        period: Period,
        limit: Int = 300
    ): Result<List<RankedItem>> {
        return try {
            val periodKey = period.toPeriodKey()
            val top = playRepository.getTopEntities(
                entityType = EntityType.SONG,
                period     = period,
                periodKey  = periodKey,
                limit      = limit
            )
            val ranked = top.mapIndexed { index, (entityId, playCount) ->
                RankedItem(
                    position     = index + 1,
                    entityId     = entityId,
                    entityType   = EntityType.SONG,
                    playCount    = playCount,
                    listenTimeMs = 0L  // Enrichi si nécessaire
                )
            }
            Result.Success(ranked)
        } catch (e: Exception) {
            Result.Error("Erreur chargement top tracks : ${e.message}", e)
        }
    }
}