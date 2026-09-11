// data/local/database/dao/PlayDao.kt
package com.novalisten.app.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.novalisten.app.data.local.database.entity.PlayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(play: PlayEntity): Long

    /** Écoutes récentes pour l'UI live */
    @Query("""
        SELECT * FROM plays
        WHERE is_valid = 1
        ORDER BY started_at DESC
        LIMIT :limit
    """)
    fun observeRecent(limit: Int = 20): Flow<List<PlayEntity>>

    /** Compte les écoutes valides d'un artiste sur une période */
    @Query("""
        SELECT COUNT(*) FROM plays
        WHERE is_valid = 1
        AND artist_id = :entityId
        AND started_at >= :startMs
        AND started_at < :endMs
    """)
    suspend fun countValidPlaysForArtist(
        entityId: Long,
        startMs: Long,
        endMs: Long
    ): Int

    /** Compte les écoutes valides d'un morceau sur une période */
    @Query("""
        SELECT COUNT(*) FROM plays
        WHERE is_valid = 1
        AND track_id = :entityId
        AND started_at >= :startMs
        AND started_at < :endMs
    """)
    suspend fun countValidPlaysForTrack(
        entityId: Long,
        startMs: Long,
        endMs: Long
    ): Int

    /** Temps d'écoute total d'un artiste sur une période */
    @Query("""
        SELECT COALESCE(SUM(duration_listened), 0) FROM plays
        WHERE is_valid = 1
        AND artist_id = :artistId
        AND started_at >= :startMs
        AND started_at < :endMs
    """)
    suspend fun sumListenTimeForArtist(
        artistId: Long,
        startMs: Long,
        endMs: Long
    ): Long

    /** Total écoutes valides d'un artiste (all time) — Panthéon Option B */
    @Query("""
        SELECT COUNT(*) FROM plays
        WHERE is_valid = 1
        AND artist_id = :artistId
    """)
    suspend fun totalValidPlaysForArtist(artistId: Long): Int

    /** Top N artistes par écoutes sur une période */
    @Query("""
        SELECT artist_id AS entity_id, COUNT(*) AS play_count
        FROM plays
        WHERE is_valid = 1
        AND started_at >= :startMs
        AND started_at < :endMs
        GROUP BY artist_id
        ORDER BY play_count DESC, SUM(duration_listened) DESC
        LIMIT :limit
    """)
    suspend fun topArtists(
        startMs: Long,
        endMs: Long,
        limit: Int
    ): List<EntityPlayCount>

    /** Top N morceaux par écoutes sur une période */
    @Query("""
        SELECT track_id AS entity_id, COUNT(*) AS play_count
        FROM plays
        WHERE is_valid = 1
        AND started_at >= :startMs
        AND started_at < :endMs
        GROUP BY track_id
        ORDER BY play_count DESC, SUM(duration_listened) DESC
        LIMIT :limit
    """)
    suspend fun topTracks(
        startMs: Long,
        endMs: Long,
        limit: Int
    ): List<EntityPlayCount>

    /** Top N albums par écoutes sur une période */
    @Query("""
        SELECT album_id AS entity_id, COUNT(*) AS play_count
        FROM plays
        WHERE is_valid = 1
        AND album_id IS NOT NULL
        AND started_at >= :startMs
        AND started_at < :endMs
        GROUP BY album_id
        ORDER BY play_count DESC, SUM(duration_listened) DESC
        LIMIT :limit
    """)
    suspend fun topAlbums(
        startMs: Long,
        endMs: Long,
        limit: Int
    ): List<EntityPlayCount>

    /** Écoutes d'une session */
    @Query("""
        SELECT * FROM plays
        WHERE session_id = :sessionId
        ORDER BY started_at ASC
    """)
    suspend fun getBySession(sessionId: String): List<PlayEntity>

    /** Dates distinctes avec au moins 1 écoute valide (pour streak) */
    @Query("""
        SELECT DISTINCT DATE(started_at / 1000, 'unixepoch') AS day
        FROM plays
        WHERE is_valid = 1
        ORDER BY day DESC
    """)
    suspend fun getDistinctDaysWithPlays(): List<String>

    /** Première écoute enregistrée (pour calcul déblocage Awards) */
    @Query("SELECT MIN(started_at) FROM plays WHERE is_valid = 1")
    suspend fun getFirstPlayTimestamp(): Long?
}

/** Résultat intermédiaire pour les requêtes top N */
data class EntityPlayCount(
    val entity_id: Long,
    val play_count: Int
)