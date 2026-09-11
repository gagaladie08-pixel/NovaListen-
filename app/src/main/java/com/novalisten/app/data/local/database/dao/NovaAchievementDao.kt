// data/local/database/dao/NovaAchievementDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.NovaAchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NovaAchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: NovaAchievementEntity): Long

    @Update
    suspend fun update(achievement: NovaAchievementEntity)

    /** Award par catégorie et année */
    @Query("""
        SELECT * FROM nova_achievements
        WHERE type = 'AWARD'
        AND award_category = :category
        AND year = :year
        LIMIT 1
    """)
    suspend fun getAward(category: String, year: Int): NovaAchievementEntity?

    /** Record par catégorie et période */
    @Query("""
        SELECT * FROM nova_achievements
        WHERE type = 'RECORD'
        AND record_category = :category
        AND (period_filter = :periodFilter OR (period_filter IS NULL AND :periodFilter IS NULL))
        LIMIT 1
    """)
    suspend fun getRecord(
        category: String,
        periodFilter: String?
    ): NovaAchievementEntity?

    /** Tous les awards d'une année */
    @Query("""
        SELECT * FROM nova_achievements
        WHERE type = 'AWARD'
        AND year = :year
        ORDER BY award_category ASC
    """)
    fun observeAwards(year: Int): Flow<List<NovaAchievementEntity>>

    /** Tous les records */
    @Query("""
        SELECT * FROM nova_achievements
        WHERE type = 'RECORD'
        ORDER BY record_category ASC
    """)
    fun observeRecords(): Flow<List<NovaAchievementEntity>>
}