// domain/usecase/pantheon/GetPantheonSoonUseCase.kt
package com.novalisten.app.domain.usecase.pantheon

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.PantheonState
import com.novalisten.app.domain.repository.IPantheonRepository
import javax.inject.Inject

/**
 * Section "Bientôt dans le Panthéon".
 *
 * Artistes proches d'un nouveau statut —
 * affichés en bas de l'écran Panthéon.
 *
 * Message généré : "Il manque X écoutes pour devenir Star"
 */
class GetPantheonSoonUseCase @Inject constructor(
    private val pantheonRepository: IPantheonRepository
) {

    suspend operator fun invoke(): Result<List<PantheonState>> {
        return try {
            val soon = pantheonRepository.getArtistsSoon()
            Result.Success(soon)
        } catch (e: Exception) {
            Result.Error("Erreur section Bientôt : ${e.message}", e)
        }
    }
}