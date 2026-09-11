// data/local/database/dao/PantheonDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.PantheonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PantheonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pantheon: PantheonEntity)

    @Query("SELECT * FROM pantheon WHERE artist_id = :artistId LIMIT 1")
    suspend fun getByArtist(artistId: Long): PantheonEntity?

    /** Tous les artistes du Panthéon triés par statut (Mythique en tête) */
    @Query("""
        SELECT * FROM pantheon
        ORDER BY
            CASE status
                WHEN 'MYTHIQUE'   THEN 5
                WHEN 'LEGENDE'    THEN 4
                WHEN 'MEGASTAR'   THEN 3
                WHEN 'SUPERSTAR'  THEN 2
                WHEN 'STAR'       THEN 1
                ELSE 0
            END DESC,
            total_plays DESC
    """)
    fun observeAll(): Flow<List<PantheonEntity>>

    /** Artistes par statut */
    @Query("""
        SELECT * FROM pantheon
        WHERE status = :status
        ORDER BY total_plays DESC
    """)
    suspend fun getByStatus(status: String): List<PantheonEntity>

    /** Artistes proches d'un nouveau statut (section "Bientôt…") */
    @Query("""
        SELECT * FROM pantheon
        ORDER BY total_plays DESC
        LIMIT 10
    """)
    suspend fun getSoon(): List<PantheonEntity>
}