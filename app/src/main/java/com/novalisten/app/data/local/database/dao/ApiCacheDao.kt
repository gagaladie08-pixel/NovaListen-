// data/local/database/dao/ApiCacheDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.ApiCacheEntity

@Dao
interface ApiCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: ApiCacheEntity)

    @Update
    suspend fun update(cache: ApiCacheEntity)

    /** Récupère un cache valide (non expiré) */
    @Query("""
        SELECT * FROM api_cache
        WHERE api_source = :source
        AND endpoint_key = :endpointKey
        AND expires_at > :now
        LIMIT 1
    """)
    suspend fun getValid(
        source: String,
        endpointKey: String,
        now: Long = System.currentTimeMillis()
    ): ApiCacheEntity?

    /** Incrémente le hit count */
    @Query("""
        UPDATE api_cache
        SET hit_count = hit_count + 1
        WHERE api_source = :source
        AND endpoint_key = :endpointKey
    """)
    suspend fun incrementHitCount(source: String, endpointKey: String)

    /** Supprime les caches expirés (nettoyage périodique) */
    @Query("""
        DELETE FROM api_cache
        WHERE expires_at < :now
    """)
    suspend fun deleteExpired(now: Long = System.currentTimeMillis())
}