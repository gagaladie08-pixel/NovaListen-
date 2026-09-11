// domain/usecase/stats/GetTopArtistsUseCase.kt
package com.novalisten.app.domain.usecase.stats

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.RankedItem
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.usecase.stats.PeriodKeyHelper.toPeriodKey
import javax.inject.Inject

/**
 * Retourne le Top 300 artistes pour une période donnée.
 */
class GetTopArtistsUseCase @Inject constructor(
    private val playRepository: IPlayRepository
) {

    suspend operator fun invoke(
        period: Period,
        limit: Int = 300
    ): Result<List<RankedItem>> {
        return try {
            val periodKey = period.toPeriodKey()
            val top = playRepository.getTopEntities(
                entityType = EntityType.ARTIST,
                period     = period,
                periodKey  = periodKey,
                limit      = limit
            )
            val ranked = top.mapIndexed { index, (entityId, playCount) ->
                RankedItem(
                    position     = index + 1,
                    entityId     = entityId,
                    entityType   = EntityType.ARTIST,
                    playCount    = playCount,
                    listenTimeMs = 0L
                )
            }
            Result.Success(ranked)
        } catch (e: Exception) {
            Result.Error("Erreur chargement top artistes : ${e.message}", e)
        }
    }
}