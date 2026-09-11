// domain/usecase/billboard/GetBillboardSnapshotUseCase.kt
package com.novalisten.app.domain.usecase.billboard

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.BillboardSnapshot
import com.novalisten.app.domain.model.enums.ChartType
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IBillboardRepository
import com.novalisten.app.domain.usecase.stats.PeriodKeyHelper.toPeriodKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Retourne le snapshot Billboard actuel.
 * Flow pour observation en temps réel.
 */
class GetBillboardSnapshotUseCase @Inject constructor(
    private val billboardRepository: IBillboardRepository
) {

    /**
     * Flux du snapshot le plus récent.
     */
    operator fun invoke(
        chartType: ChartType,
        period: Period
    ): Flow<Result<BillboardSnapshot?>> =
        billboardRepository.observeLatestSnapshot(chartType, period)
            .map { snapshot ->
                Result.Success(snapshot) as Result<BillboardSnapshot?>
            }
            .catch { e ->
                emit(Result.Error("Erreur Billboard : ${e.message}", e))
            }

    /**
     * Snapshot d'une période spécifique (navigation historique).
     */
    suspend fun forPeriodKey(
        chartType: ChartType,
        period: Period,
        periodKey: String
    ): Result<BillboardSnapshot?> {
        return try {
            val snapshot = billboardRepository.getSnapshot(
                chartType, period, periodKey
            )
            Result.Success(snapshot)
        } catch (e: Exception) {
            Result.Error("Erreur snapshot Billboard : ${e.message}", e)
        }
    }
}