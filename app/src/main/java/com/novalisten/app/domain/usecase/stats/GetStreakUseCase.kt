// domain/usecase/stats/GetStreakUseCase.kt
package com.novalisten.app.domain.usecase.stats

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.repository.IPlayRepository
import javax.inject.Inject

/**
 * Résultat du streak.
 */
data class StreakResult(
    val currentStreak: Int,  // Jours consécutifs actuels
    val longestStreak: Int   // Record historique
)

/**
 * Calcule le streak actuel et le record historique.
 *
 * Streak = nombre de jours consécutifs avec
 * au moins 1 écoute valide.
 *
 * Règle : si aujourd'hui n'a pas encore d'écoute,
 * le streak actuel = 0 (mais le record est conservé).
 */
class GetStreakUseCase @Inject constructor(
    private val playRepository: IPlayRepository
) {

    suspend operator fun invoke(): Result<StreakResult> {
        return try {
            val current = playRepository.getCurrentStreak()
            val longest = playRepository.getLongestStreak()
            Result.Success(
                StreakResult(
                    currentStreak = current,
                    longestStreak = maxOf(current, longest)
                )
            )
        } catch (e: Exception) {
            Result.Error("Erreur calcul streak : ${e.message}", e)
        }
    }
}