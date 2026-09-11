// domain/usecase/certifications/RecomputeCertificationsUseCase.kt
package com.novalisten.app.domain.usecase.certifications

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.CertificationHistoryEntry
import com.novalisten.app.domain.model.entity.CertificationState
import com.novalisten.app.domain.model.enums.CertificationLevel
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.ICertificationRepository
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.usecase.stats.PeriodKeyHelper.toPeriodKey
import javax.inject.Inject

/**
 * Recalcule les certifications après chaque écoute valide.
 *
 * Appelé dans le pipeline :
 * PlaySaver → RecomputeCertificationsUseCase → ComputePantheonStatusUseCase
 *
 * Seuils Chansons :
 *   Argent → 25 | Or → 50 | Platine → 100
 *   Diamant → 350 (+350 par multiplicateur)
 *
 * Seuils Albums :
 *   Argent → 50 | Or → 100 | Platine → 200
 *   Diamant → 700 (+700 par multiplicateur)
 *
 * Écoutes album = somme des écoutes de tous ses titres.
 */
class RecomputeCertificationsUseCase @Inject constructor(
    private val certificationRepository: ICertificationRepository,
    private val playRepository: IPlayRepository
) {

    /**
     * @param entityId   ID du morceau ou album
     * @param entityType SONG ou ALBUM
     */
    suspend operator fun invoke(
        entityId: Long,
        entityType: EntityType
    ): Result<CertificationState?> {
        if (entityType == EntityType.ARTIST) {
            return Result.Success(null) // Pas de certif pour les artistes
        }

        return try {
            // 1. Compte les écoutes totales (all time)
            val playCount = playRepository.getPlayCount(
                entityId   = entityId,
                entityType = entityType,
                period     = Period.GLOBAL,
                periodKey  = Period.GLOBAL.toPeriodKey()
            )

            // 2. Calcule le niveau actuel
            val (level, multiplier) = computeLevel(playCount, entityType)
                ?: return Result.Success(null)  // Pas encore certifié

            // 3. Calcule le prochain seuil
            val nextThreshold = computeNextThreshold(
                playCount, level, multiplier, entityType
            )

            // 4. Récupère la certification existante
            val existing = certificationRepository.getCertification(
                entityId, entityType
            )

            // 5. Si même niveau → pas de changement
            if (existing?.level == level &&
                existing.diamondMultiplier == multiplier) {
                return Result.Success(existing)
            }

            // 6. Crée ou met à jour la certification
            val newEntry = CertificationHistoryEntry(
                level             = level,
                diamondMultiplier = multiplier,
                playCountAtTime   = playCount,
                certifiedAt       = System.currentTimeMillis()
            )

            val history = (existing?.history ?: emptyList()) + newEntry

            val newState = CertificationState(
                id                 = existing?.id ?: 0,
                entityType         = entityType,
                entityId           = entityId,
                level              = level,
                diamondMultiplier  = multiplier,
                playCountAtCert    = playCount,
                certifiedAt        = System.currentTimeMillis(),
                nextThresholdDelta = nextThreshold - playCount,
                history            = history
            )

            certificationRepository.upsertCertification(newState)
            Result.Success(newState)

        } catch (e: Exception) {
            Result.Error("Erreur recalcul certification : ${e.message}", e)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    /**
     * Calcule le niveau et multiplicateur pour un nombre d'écoutes donné.
     * Retourne null si pas encore certifié.
     */
    private fun computeLevel(
        playCount: Int,
        entityType: EntityType
    ): Pair<CertificationLevel, Int>? {
        val thresholds = if (entityType == EntityType.SONG) {
            listOf(
                CertificationLevel.SILVER   to 25,
                CertificationLevel.GOLD     to 50,
                CertificationLevel.PLATINUM to 100,
                CertificationLevel.DIAMOND  to 350
            )
        } else {
            listOf(
                CertificationLevel.SILVER   to 50,
                CertificationLevel.GOLD     to 100,
                CertificationLevel.PLATINUM to 200,
                CertificationLevel.DIAMOND  to 700
            )
        }

        // Pas encore certifié
        if (playCount < thresholds.first().second) return null

        // Vérifie si Diamant (multi-diamant possible)
        val diamondThreshold = thresholds.last().second
        if (playCount >= diamondThreshold) {
            val multiplier = playCount / diamondThreshold
            return CertificationLevel.DIAMOND to multiplier
        }

        // Trouve le niveau correspondant
        var currentLevel = thresholds.first().first
        for ((level, threshold) in thresholds) {
            if (playCount >= threshold) currentLevel = level
            else break
        }
        return currentLevel to 1
    }

    /**
     * Calcule le prochain seuil.
     */
    private fun computeNextThreshold(
        playCount: Int,
        level: CertificationLevel,
        multiplier: Int,
        entityType: EntityType
    ): Int {
        val diamondBase = if (entityType == EntityType.SONG) 350 else 700
        return when (level) {
            CertificationLevel.SILVER   ->
                if (entityType == EntityType.SONG) 50 else 100
            CertificationLevel.GOLD     ->
                if (entityType == EntityType.SONG) 100 else 200
            CertificationLevel.PLATINUM ->
                diamondBase
            CertificationLevel.DIAMOND  ->
                diamondBase * (multiplier + 1)
        }
    }
}