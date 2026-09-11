// domain/usecase/pantheon/GetPantheonListUseCase.kt
package com.novalisten.app.domain.usecase.pantheon

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.PantheonState
import com.novalisten.app.domain.repository.IPantheonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Retourne la liste du Panthéon triée par statut.
 * Mythique en tête, Star en bas.
 * Artistes sans statut = invisibles.
 *
 * Retourne un Flow pour observation en temps réel.
 */
class GetPantheonListUseCase @Inject constructor(
    private val pantheonRepository: IPantheonRepository
) {

    operator fun invoke(): Flow<Result<List<PantheonState>>> =
        pantheonRepository.observePantheon()
            .map { list -> Result.Success(list) as Result<List<PantheonState>> }
            .catch { e -> emit(Result.Error("Erreur Panthéon : ${e.message}", e)) }
}