// data/local/database/dao/ArtistDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.ArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: ArtistEntity): Long

    @Update
    suspend fun update(artist: ArtistEntity)

    @Query("SELECT * FROM artists WHERE id = :id")
    suspend fun getById(id: Long): ArtistEntity?

    @Query("""
        SELECT * FROM artists
        WHERE normalized_name = :normalizedName
        LIMIT 1
    """)
    suspend fun findByName(normalizedName: String): ArtistEntity?

    @Query("SELECT * FROM artists ORDER BY name ASC")
    fun observeAll(): Flow<List<ArtistEntity>>

    @Query("""
        SELECT * FROM artists
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name ASC
        LIMIT 50
    """)
    suspend fun search(query: String): List<ArtistEntity>
}