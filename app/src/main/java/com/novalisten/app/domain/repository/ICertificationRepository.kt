// domain/repository/ICertificationRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.CertificationState
import com.novalisten.app.domain.model.enums.CertificationLevel
import com.novalisten.app.domain.model.enums.EntityType
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Certification.
 * Songs et Albums uniquement (pas d'artistes).
 */
interface ICertificationRepository {

    /** Certification actuelle d'une entité */
    suspend fun getCertification(
        entityId: Long,
        entityType: EntityType
    ): CertificationState?

    /** Sauvegarde ou met à jour une certification */
    suspend fun upsertCertification(state: CertificationState)

    /** Toutes les certifications triées (niveau DESC, écoutes DESC) */
    fun observeCertifications(entityType: EntityType): Flow<List<CertificationState>>

    /** Top 5 entités les plus proches du prochain palier (Radar) */
    suspend fun getRadarEntities(
        entityType: EntityType,
        limit: Int = 5
    ): List<CertificationState>

    /** Entités ayant atteint un niveau spécifique */
    suspend fun getEntitiesByLevel(
        entityType: EntityType,
        level: CertificationLevel
    ): List<CertificationState>
}