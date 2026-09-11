// data/local/database/dao/AlbumDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.AlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: AlbumEntity): Long

    @Update
    suspend fun update(album: AlbumEntity)

    @Query("SELECT * FROM albums WHERE id = :id")
    suspend fun getById(id: Long): AlbumEntity?

    @Query("""
        SELECT * FROM albums
        WHERE normalized_title = :normalizedTitle
        AND artist_id = :artistId
        LIMIT 1
    """)
    suspend fun findByTitleAndArtist(
        normalizedTitle: String,
        artistId: Long
    ): AlbumEntity?

    @Query("""
        SELECT * FROM albums
        WHERE artist_id = :artistId
        ORDER BY release_date DESC
    """)
    suspend fun getByArtist(artistId: Long): List<AlbumEntity>

    @Query("""
        SELECT * FROM albums
        WHERE artist_id = :artistId
        ORDER BY release_date DESC
    """)
    fun observeByArtist(artistId: Long): Flow<List<AlbumEntity>>

    @Query("""
        SELECT * FROM albums
        WHERE title LIKE '%' || :query || '%'
        ORDER BY title ASC
        LIMIT 50
    """)
    suspend fun search(query: String): List<AlbumEntity>
}