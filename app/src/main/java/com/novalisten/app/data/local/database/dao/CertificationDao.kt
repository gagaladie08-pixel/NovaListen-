// data/local/database/dao/CertificationDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.CertificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CertificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(certification: CertificationEntity)

    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        AND entity_id = :entityId
        LIMIT 1
    """)
    suspend fun get(entityType: String, entityId: Long): CertificationEntity?

    /** Toutes les certifications d'un type (SONG ou ALBUM) */
    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        ORDER BY
            CASE level
                WHEN 'DIAMOND'  THEN 4
                WHEN 'PLATINUM' THEN 3
                WHEN 'GOLD'     THEN 2
                WHEN 'SILVER'   THEN 1
                ELSE 0
            END DESC,
            diamond_multiplier DESC,
            play_count_at_cert DESC
    """)
    fun observeAll(entityType: String): Flow<List<CertificationEntity>>

    /** Top 5 entités les plus proches du prochain palier (Radar) */
    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        ORDER BY next_threshold_delta ASC
        LIMIT :limit
    """)
    suspend fun getRadar(entityType: String, limit: Int = 5): List<CertificationEntity>

    /** Entités par niveau */
    @Query("""
        SELECT * FROM certifications
        WHERE entity_type = :entityType
        AND level = :level
        ORDER BY play_count_at_cert DESC
    """)
    suspend fun getByLevel(
        entityType: String,
        level: String
    ): List<CertificationEntity>

    /** Compte les certifications d'un artiste par niveau (pour Panthéon) */
    @Query("""
        SELECT COUNT(*) FROM certifications c
        INNER JOIN tracks t ON c.entity_id = t.id
        WHERE c.entity_type = 'SONG'
        AND t.artist_id = :artistId
        AND (
            CASE c.level
                WHEN 'DIAMOND'  THEN 4
                WHEN 'PLATINUM' THEN 3
                WHEN 'GOLD'     THEN 2
                WHEN 'SILVER'   THEN 1
                ELSE 0
            END
        ) >= :minLevelRank
    """)
    suspend fun countCertifiedSongsForArtist(
        artistId: Long,
        minLevelRank: Int
    ): Int

    /** Compte les albums certifiés d'un artiste par niveau minimum */
    @Query("""
        SELECT COUNT(*) FROM certifications c
        INNER JOIN albums a ON c.entity_id = a.id
        WHERE c.entity_type = 'ALBUM'
        AND a.artist_id = :artistId
        AND (
            CASE c.level
                WHEN 'DIAMOND'  THEN 4
                WHEN 'PLATINUM' THEN 3
                WHEN 'GOLD'     THEN 2
                WHEN 'SILVER'   THEN 1
                ELSE 0
            END
        ) >= :minLevelRank
    """)
    suspend fun countCertifiedAlbumsForArtist(
        artistId: Long,
        minLevelRank: Int
    ): Int
}