// data/repository/AchievementRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.NovaAchievementDao
import com.novalisten.app.data.local.database.dao.PlayDao
import com.novalisten.app.data.local.database.entity.NovaAchievementEntity
import com.novalisten.app.domain.model.entity.AwardStatus
import com.novalisten.app.domain.model.entity.NovaAchievement
import com.novalisten.app.domain.model.enums.AchievementType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.NovaAwardCategory
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IAchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AchievementRepositoryImpl @Inject constructor(
    private val achievementDao: NovaAchievementDao,
    private val playDao: PlayDao
) : IAchievementRepository {

    override suspend fun saveAchievement(achievement: NovaAchievement): Long =
        achievementDao.insert(achievement.toEntity())

    override suspend fun updateAchievement(achievement: NovaAchievement) =
        achievementDao.update(achievement.toEntity())

    override suspend fun getAward(
        category: NovaAwardCategory,
        year: Int
    ): NovaAchievement? =
        achievementDao.getAward(category.name, year)?.toDomain()

    override suspend fun getRecord(
        category: String,
        period: Period?
    ): NovaAchievement? =
        achievementDao.getRecord(category, period?.name)?.toDomain()

    override fun observeAwards(year: Int): Flow<List<NovaAchievement>> =
        achievementDao.observeAwards(year)
            .map { list -> list.map { it.toDomain() } }

    override fun observeRecords(): Flow<List<NovaAchievement>> =
        achievementDao.observeRecords()
            .map { list -> list.map { it.toDomain() } }

    /**
     * Déblocage Awards : 2 mois d'utilisation minimum.
     * Calcul : date première écoute → aujourd'hui >= 60 jours.
     */
    override suspend fun isNovaAwardsUnlocked(): Boolean {
        val firstPlay = playDao.getFirstPlayTimestamp() ?: return false
        val diffDays  = TimeUnit.MILLISECONDS.toDays(
            System.currentTimeMillis() - firstPlay
        )
        return diffDays >= 60
    }

    // ── Mapper Entity → Domain ─────────────────────────────────────────────

    private fun NovaAchievementEntity.toDomain(): NovaAchievement =
        NovaAchievement(
            id             = id,
            type           = AchievementType.valueOf(type),
            year           = year,
            entityType     = entityType?.let { EntityType.valueOf(it) },
            entityId       = entityId,
            awardCategory  = awardCategory?.let { NovaAwardCategory.valueOf(it) },
            awardStatus    = awardStatus?.let { AwardStatus.valueOf(it) },
            recordCategory = recordCategory,
            periodFilter   = periodFilter?.let { Period.valueOf(it) },
            valueNumeric   = valueNumeric,
            valueDisplay   = valueDisplay,
            achievedAt     = achievedAt,
            top10Json      = top10Json
        )

    // ── Mapper Domain → Entity ─────────────────────────────────────────────

    private fun NovaAchievement.toEntity(): NovaAchievementEntity =
        NovaAchievementEntity(
            id             = id,
            type           = type.name,
            year           = year,
            entityType     = entityType?.name,
            entityId       = entityId,
            awardCategory  = awardCategory?.name,
            awardStatus    = awardStatus?.name,
            recordCategory = recordCategory,
            periodFilter   = periodFilter?.name,
            valueNumeric   = valueNumeric,
            valueDisplay   = valueDisplay,
            achievedAt     = achievedAt,
            top10Json      = top10Json
        )
}