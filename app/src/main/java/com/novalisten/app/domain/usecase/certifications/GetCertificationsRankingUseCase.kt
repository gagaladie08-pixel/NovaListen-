// domain/usecase/certifications/GetCertificationsRankingUseCase.kt
package com.novalisten.app.domain.usecase.certifications

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.CertificationState
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.repository.ICertificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Retourne la liste des certifications triée.
 *
 * Tri :
 * → Niveau DESC (Diamant > Platine > Or > Argent)
 * → Multiplicateur Diamant DESC (x3 > x2 > x1)
 * → Écoutes DESC (départage)
 *
 * Onglets : SONG / ALBUM
 */
class GetCertificationsRankingUseCase @Inject constructor(
    private val certificationRepository: ICertificationRepository
) {

    /**
     * Retourne un Flow pour observation en temps réel.
     */
    operator fun invoke(
        entityType: EntityType
    ): Flow<Result<List<CertificationState>>> =
        certificationRepository.observeCertifications(entityType)
            .map { list -> Result.Success(list) as Result<List<CertificationState>> }
            .catch { e -> emit(Result.Error("Erreur certifications : ${e.message}", e)) }
}