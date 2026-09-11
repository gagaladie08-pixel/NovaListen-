// data/local/database/dao/BillboardDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.novalisten.app.data.local.database.entity.BillboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BillboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: BillboardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entries: List<BillboardEntity>)

    /** Snapshot complet d'une période/chart */
    @Query("""
        SELECT * FROM billboard
        WHERE chart_type = :chartType
        AND period_type = :periodType
        AND period_key = :periodKey
        ORDER BY rank ASC
    """)
    suspend fun getSnapshot(
        chartType: String,
        periodType: String,
        periodKey: String
    ): List<BillboardEntity>

    /** Flux du snapshot le plus récent */
    @Query("""
        SELECT * FROM billboard
        WHERE chart_type = :chartType
        AND period_type = :periodType
        AND period_key = (
            SELECT MAX(period_key) FROM billboard
            WHERE chart_type = :chartType
            AND period_type = :periodType
        )
        ORDER BY rank ASC
    """)
    fun observeLatest(
        chartType: String,
        periodType: String
    ): Flow<List<BillboardEntity>>

    /** Historique d'une entité dans un chart */
    @Query("""
        SELECT * FROM billboard
        WHERE entity_id = :entityId
        AND chart_type = :chartType
        ORDER BY period_key DESC
    """)
    suspend fun getEntityHistory(
        entityId: Long,
        chartType: String
    ): List<BillboardEntity>

    /** Peak rank historique */
    @Query("""
        SELECT MIN(rank) FROM billboard
        WHERE entity_id = :entityId
        AND chart_type = :chartType
    """)
    suspend fun getPeakRank(entityId: Long, chartType: String): Int?

    /** Semaines totales dans un chart */
    @Query("""
        SELECT COUNT(DISTINCT period_key) FROM billboard
        WHERE entity_id = :entityId
        AND chart_type = :chartType
        AND period_type = 'WEEKLY'
    """)
    suspend fun getWeeksOnChart(entityId: Long, chartType: String): Int

    /** Entrées Hall of Fame */
    @Query("""
        SELECT * FROM billboard
        WHERE chart_type = :chartType
        AND (hof_direct_debut = 1 OR hof_long_run = 1
             OR hof_triple_debut = 1 OR hof_legendary_run = 1)
        ORDER BY hof_entry_date DESC
    """)
    suspend fun getHallOfFameEntries(chartType: String): List<BillboardEntity>

    /** Semaines consécutives à #1 (pour Long Run) */
    @Query("""
        SELECT COUNT(*) FROM billboard
        WHERE entity_id = :entityId
        AND chart_type = :chartType
        AND period_type = 'WEEKLY'
        AND rank = 1
    """)
    suspend fun countWeeksAtNumber1(entityId: Long, chartType: String): Int
}