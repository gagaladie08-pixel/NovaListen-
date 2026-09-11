// domain/usecase/certifications/GetCertificationRadarUseCase.kt
package com.novalisten.app.domain.usecase.certifications

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.CertificationState
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.repository.ICertificationRepository
import javax.inject.Inject

/**
 * Retourne le Radar : Top 5 entités les plus proches
 * du prochain palier de certification.
 *
 * Affiché en permanence en haut de l'onglet Certifications.
 * Tri : nextThresholdDelta ASC (le plus proche en premier).
 */
class GetCertificationRadarUseCase @Inject constructor(
    private val certificationRepository: ICertificationRepository
) {

    suspend operator fun invoke(
        entityType: EntityType,
        limit: Int = 5
    ): Result<List<CertificationState>> {
        return try {
            val radar = certificationRepository.getRadarEntities(
                entityType, limit
            )
            Result.Success(radar)
        } catch (e: Exception) {
            Result.Error("Erreur radar certifications : ${e.message}", e)
        }
    }
}