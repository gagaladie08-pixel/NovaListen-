// data/local/database/dao/TrackDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.novalisten.app.data.local.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: TrackEntity): Long

    @Update
    suspend fun update(track: TrackEntity)

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getById(id: Long): TrackEntity?

    @Query("""
        SELECT * FROM tracks
        WHERE normalized_title = :normalizedTitle
        AND artist_id = :artistId
        LIMIT 1
    """)
    suspend fun findByTitleAndArtist(
        normalizedTitle: String,
        artistId: Long
    ): TrackEntity?

    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun observeAll(): Flow<List<TrackEntity>>

    @Query("""
        SELECT * FROM tracks
        WHERE title LIKE '%' || :query || '%'
        OR normalized_title LIKE '%' || :query || '%'
        ORDER BY title ASC
        LIMIT 50
    """)
    suspend fun search(query: String): List<TrackEntity>

    @Query("""
        INSERT OR REPLACE INTO tracks
        (id, title, normalized_title, artist_id, album_id,
         duration_ms, genre, release_date, is_explicit,
         spotify_id, musicbrainz_id, isrc, created_at, updated_at)
        VALUES
        (:id, :title, :normalizedTitle, :artistId, :albumId,
         :durationMs, :genre, :releaseDate, :isExplicit,
         :spotifyId, :musicBrainzId, :isrc,
         COALESCE((SELECT created_at FROM tracks WHERE id = :id), :now),
         :now)
    """)
    suspend fun upsert(
        id: Long,
        title: String,
        normalizedTitle: String,
        artistId: Long,
        albumId: Long?,
        durationMs: Long,
        genre: String?,
        releaseDate: String?,
        isExplicit: Boolean,
        spotifyId: String?,
        musicBrainzId: String?,
        isrc: String?,
        now: Long = System.currentTimeMillis()
    ): Long
}