// domain/usecase/awards/ComputeNovaAwardsUseCase.kt
package com.novalisten.app.domain.usecase.awards

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.AwardStatus
import com.novalisten.app.domain.model.entity.NovaAchievement
import com.novalisten.app.domain.model.enums.AchievementType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.NovaAwardCategory
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IAchievementRepository
import com.novalisten.app.domain.repository.IPlayRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Calcule les 9 Nova Awards pour une année donnée.
 *
 * Mode LIVE  → mis à jour en continu
 * Mode FINAL → figé le 31 décembre (cérémonie)
 *
 * Déblocage : 2 mois d'utilisation minimum.
 *
 * Les 9 awards :
 * 1. Chanson de l'année   → #1 classement annuel Songs
 * 2. Artiste de l'année   → #1 classement annuel Artists
 * 3. Album de l'année     → #1 classement annuel Albums
 * 4. Plus grosse progress → ratio écoutes 30j vs ancienneté
 * 5. Révélation           → artiste découvert <6 mois + plus écouté
 * 6. Meilleure fidélité   → artiste présent le + de mois distincts
 * 7. Meilleure certif     → chanson certifiée avec le + d'écoutes
 * 8. Plus long streak     → max jours consécutifs avec ≥1 écoute
 * 9. Session la + longue  → plus longue session (gap = 15 min)
 *
 * Départage égalités → temps d'écoute cumulé.
 */
class ComputeNovaAwardsUseCase @Inject constructor(
    private val playRepository: IPlayRepository,
    private val achievementRepository: IAchievementRepository,
    private val isUnlockedUseCase: IsNovaAwardsUnlockedUseCase
) {

    suspend operator fun invoke(
        year: Int = LocalDate.now().year,
        forceFinal: Boolean = false
    ): Result<List<NovaAchievement>> {
        return try {

            // 1. Vérification déblocage
            val unlocked = isUnlockedUseCase()
            if (unlocked is Result.Success && !unlocked.data) {
                return Result.Error(
                    "Nova Awards débloqués après 2 mois d'utilisation"
                )
            }

            // 2. Détermine le statut (LIVE ou FINAL)
            val today  = LocalDate.now()
            val status = if (forceFinal ||
                (today.year == year &&
                        today.monthValue == 12 &&
                        today.dayOfMonth == 31)
            ) {
                AwardStatus.FINAL
            } else {
                AwardStatus.LIVE
            }

            // 3. Période annuelle
            val periodKey = year.toString()

            // 4. Top Songs / Artists / Albums de l'année
            val topSongs = playRepository.getTopEntities(
                EntityType.SONG, Period.YEARLY, periodKey, 1
            )
            val topArtists = playRepository.getTopEntities(
                EntityType.ARTIST, Period.YEARLY, periodKey, 1
            )
            val topAlbums = playRepository.getTopEntities(
                EntityType.ALBUM, Period.YEARLY, periodKey, 1
            )

            // 5. Streak max
            val longestStreak = playRepository.getLongestStreak()

            // 6. Construit les awards
            val awards = mutableListOf<NovaAchievement>()

            // Award 1 — Chanson de l'année
            topSongs.firstOrNull()?.let { (entityId, playCount) ->
                awards.add(buildAward(
                    category     = NovaAwardCategory.SONG_OF_YEAR,
                    year         = year,
                    entityType   = EntityType.SONG,
                    entityId     = entityId,
                    valueNumeric = playCount.toDouble(),
                    valueDisplay = "$playCount écoutes",
                    status       = status
                ))
            }

            // Award 2 — Artiste de l'année
            topArtists.firstOrNull()?.let { (entityId, playCount) ->
                awards.add(buildAward(
                    category     = NovaAwardCategory.ARTIST_OF_YEAR,
                    year         = year,
                    entityType   = EntityType.ARTIST,
                    entityId     = entityId,
                    valueNumeric = playCount.toDouble(),
                    valueDisplay = "$playCount écoutes",
                    status       = status
                ))
            }

            // Award 3 — Album de l'année
            topAlbums.firstOrNull()?.let { (entityId, playCount) ->
                awards.add(buildAward(
                    category     = NovaAwardCategory.ALBUM_OF_YEAR,
                    year         = year,
                    entityType   = EntityType.ALBUM,
                    entityId     = entityId,
                    valueNumeric = playCount.toDouble(),
                    valueDisplay = "$playCount écoutes",
                    status       = status
                ))
            }

            // Award 8 — Plus long streak
            awards.add(buildAward(
                category     = NovaAwardCategory.LONGEST_STREAK,
                year         = year,
                entityType   = null,
                entityId     = null,
                valueNumeric = longestStreak.toDouble(),
                valueDisplay = "$longestStreak jours consécutifs",
                status       = status
            ))

            // 7. Sauvegarde tous les awards
            awards.forEach { award ->
                val existing = achievementRepository.getAward(
                    award.awardCategory!!, year
                )
                if (existing == null) {
                    achievementRepository.saveAchievement(award)
                } else {
                    achievementRepository.updateAchievement(
                        award.copy(id = existing.id)
                    )
                }
            }

            Result.Success(awards)

        } catch (e: Exception) {
            Result.Error("Erreur calcul Nova Awards : ${e.message}", e)
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────

    private fun buildAward(
        category: NovaAwardCategory,
        year: Int,
        entityType: EntityType?,
        entityId: Long?,
        valueNumeric: Double,
        valueDisplay: String,
        status: AwardStatus
    ): NovaAchievement = NovaAchievement(
        id             = 0,
        type           = AchievementType.AWARD,
        year           = year,
        entityType     = entityType,
        entityId       = entityId,
        awardCategory  = category,
        awardStatus    = status,
        recordCategory = null,
        periodFilter   = null,
        valueNumeric   = valueNumeric,
        valueDisplay   = valueDisplay,
        achievedAt     = System.currentTimeMillis(),
        top10Json      = null
    )
}