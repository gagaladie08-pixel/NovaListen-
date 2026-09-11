// data/local/database/entity/TrackEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.novalisten.app.domain.model.entity.Track

/**
 * Table Room : tracks
 * Représente un morceau détecté par NovaListen.
 *
 * Index sur (normalized_title, artist_id) pour matching rapide
 * lors de la détection musicale.
 */
@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity        = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns  = ["artist_id"],
            onDelete      = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity        = AlbumEntity::class,
            parentColumns = ["id"],
            childColumns  = ["album_id"],
            onDelete      = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["normalized_title", "artist_id"], unique = true),
        Index(value = ["artist_id"]),
        Index(value = ["album_id"]),
        Index(value = ["spotify_id"]),
        Index(value = ["isrc"])
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "normalized_title")
    val normalizedTitle: String,

    @ColumnInfo(name = "artist_id")
    val artistId: Long,

    @ColumnInfo(name = "album_id")
    val albumId: Long? = null,

    @ColumnInfo(name = "duration_ms")
    val durationMs: Long,

    @ColumnInfo(name = "genre")
    val genre: String? = null,

    @ColumnInfo(name = "release_date")
    val releaseDate: String? = null,

    @ColumnInfo(name = "is_explicit")
    val isExplicit: Boolean = false,

    @ColumnInfo(name = "spotify_id")
    val spotifyId: String? = null,

    @ColumnInfo(name = "musicbrainz_id")
    val musicBrainzId: String? = null,

    @ColumnInfo(name = "isrc")
    val isrc: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Track = Track(
        id              = id,
        title           = title,
        normalizedTitle = normalizedTitle,
        artistId        = artistId,
        albumId         = albumId,
        durationMs      = durationMs,
        genre           = genre,
        releaseDate     = releaseDate,
        isExplicit      = isExplicit,
        spotifyId       = spotifyId,
        musicBrainzId   = musicBrainzId,
        isrc            = isrc,
        createdAt       = createdAt,
        updatedAt       = updatedAt
    )

    companion object {
        fun fromDomain(track: Track): TrackEntity = TrackEntity(
            id              = track.id,
            title           = track.title,
            normalizedTitle = track.normalizedTitle,
            artistId        = track.artistId,
            albumId         = track.albumId,
            durationMs      = track.durationMs,
            genre           = track.genre,
            releaseDate     = track.releaseDate,
            isExplicit      = track.isExplicit,
            spotifyId       = track.spotifyId,
            musicBrainzId   = track.musicBrainzId,
            isrc            = track.isrc,
            createdAt       = track.createdAt,
            updatedAt       = track.updatedAt
        )
    }
}