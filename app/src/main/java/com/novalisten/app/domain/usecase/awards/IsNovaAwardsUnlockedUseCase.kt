// domain/usecase/awards/IsNovaAwardsUnlockedUseCase.kt
package com.novalisten.app.domain.usecase.awards

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.repository.IAchievementRepository
import javax.inject.Inject

/**
 * Vérifie si les Nova Awards sont débloqués.
 *
 * Condition : 2 mois d'utilisation minimum (60 jours).
 * Calcul : date première écoute valide → aujourd'hui.
 */
class IsNovaAwardsUnlockedUseCase @Inject constructor(
    private val achievementRepository: IAchievementRepository
) {

    suspend operator fun invoke(): Result<Boolean> {
        return try {
            val unlocked = achievementRepository.isNovaAwardsUnlocked()
            Result.Success(unlocked)
        } catch (e: Exception) {
            Result.Error("Erreur vérification Awards : ${e.message}", e)
        }
    }
}