// data/repository/CertificationRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.CertificationDao
import com.novalisten.app.data.local.database.entity.CertificationEntity
import com.novalisten.app.domain.model.entity.CertificationHistoryEntry
import com.novalisten.app.domain.model.entity.CertificationState
import com.novalisten.app.domain.model.enums.CertificationLevel
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.repository.ICertificationRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CertificationRepositoryImpl @Inject constructor(
    private val certificationDao: CertificationDao,
    private val gson: Gson
) : ICertificationRepository {

    override suspend fun getCertification(
        entityId: Long,
        entityType: EntityType
    ): CertificationState? =
        certificationDao.get(entityType.name, entityId)?.toDomain()

    override suspend fun upsertCertification(state: CertificationState) =
        certificationDao.upsert(state.toEntity())

    override fun observeCertifications(
        entityType: EntityType
    ): Flow<List<CertificationState>> =
        certificationDao.observeAll(entityType.name)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getRadarEntities(
        entityType: EntityType,
        limit: Int
    ): List<CertificationState> =
        certificationDao.getRadar(entityType.name, limit)
            .map { it.toDomain() }

    override suspend fun getEntitiesByLevel(
        entityType: EntityType,
        level: CertificationLevel
    ): List<CertificationState> =
        certificationDao.getByLevel(entityType.name, level.name)
            .map { it.toDomain() }

    // ── Mappers ────────────────────────────────────────────────────────────

    private fun CertificationEntity.toDomain(): CertificationState {
        val historyType = object : TypeToken<List<CertificationHistoryEntry>>() {}.type
        val history: List<CertificationHistoryEntry> =
            try { gson.fromJson(historyJson, historyType) ?: emptyList() }
            catch (e: Exception) { emptyList() }

        return CertificationState(
            id               = id,
            entityType       = EntityType.valueOf(entityType),
            entityId         = entityId,
            level            = CertificationLevel.valueOf(level),
            diamondMultiplier = diamondMultiplier,
            playCountAtCert  = playCountAtCert,
            certifiedAt      = certifiedAt,
            nextThresholdDelta = nextThresholdDelta,
            history          = history
        )
    }

    private fun CertificationState.toEntity(): CertificationEntity =
        CertificationEntity(
            id                 = id,
            entityType         = entityType.name,
            entityId           = entityId,
            level              = level.name,
            diamondMultiplier  = diamondMultiplier,
            playCountAtCert    = playCountAtCert,
            certifiedAt        = certifiedAt,
            nextThresholdDelta = nextThresholdDelta,
            historyJson        = gson.toJson(history)
        )
}