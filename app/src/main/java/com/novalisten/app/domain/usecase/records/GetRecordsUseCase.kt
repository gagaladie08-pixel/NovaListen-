// domain/usecase/records/GetRecordsUseCase.kt
package com.novalisten.app.domain.usecase.records

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.NovaAchievement
import com.novalisten.app.domain.repository.IAchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Retourne la liste de tous les records personnels.
 *
 * 24 records au total, chacun avec son Top 10.
 *
 * Tap sur un record → popup géant ~90% écran, scrollable.
 *
 * Records basés sur l'historique Billboard (positions,
 * entrées/sorties, zones Top5/Top10/#1).
 *
 * Recalculés après chaque snapshot Billboard.
 */
class GetRecordsUseCase @Inject constructor(
    private val achievementRepository: IAchievementRepository
) {

    /**
     * Flux de tous les records (observation en temps réel).
     */
    operator fun invoke(): Flow<Result<List<NovaAchievement>>> =
        achievementRepository.observeRecords()
            .map { list -> Result.Success(list) as Result<List<NovaAchievement>> }
            .catch { e -> emit(Result.Error("Erreur records : ${e.message}", e)) }
}