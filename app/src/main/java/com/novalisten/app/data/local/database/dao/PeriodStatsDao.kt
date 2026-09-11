// data/local/database/dao/PeriodStatsDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.novalisten.app.data.local.database.entity.PeriodStatsEntity

@Dao
interface PeriodStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stat: PeriodStatsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stats: List<PeriodStatsEntity>)

    /** Top N entités sur une période */
    @Query("""
        SELECT * FROM period_stats
        WHERE period_type = :periodType
        AND period_key = :periodKey
        AND entity_type = :entityType
        ORDER BY play_count DESC, listen_time_ms DESC
        LIMIT :limit
    """)
    suspend fun getTop(
        periodType: String,
        periodKey: String,
        entityType: String,
        limit: Int
    ): List<PeriodStatsEntity>

    /** Stats d'une entité sur une période */
    @Query("""
        SELECT * FROM period_stats
        WHERE entity_type = :entityType
        AND entity_id = :entityId
        AND period_type = :periodType
        AND period_key = :periodKey
        LIMIT 1
    """)
    suspend fun getEntityStat(
        entityType: String,
        entityId: Long,
        periodType: String,
        periodKey: String
    ): PeriodStatsEntity?

    /** Mise à jour du rang après calcul */
    @Query("""
        UPDATE period_stats SET rank = :rank
        WHERE entity_type = :entityType
        AND entity_id = :entityId
        AND period_type = :periodType
        AND period_key = :periodKey
    """)
    suspend fun updateRank(
        entityType: String,
        entityId: Long,
        periodType: String,
        periodKey: String,
        rank: Int
    )
}