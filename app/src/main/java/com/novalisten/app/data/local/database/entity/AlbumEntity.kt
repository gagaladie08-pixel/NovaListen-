// data/local/database/entity/AlbumEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.novalisten.app.domain.model.entity.Album
import com.novalisten.app.domain.model.entity.AlbumType

/**
 * Table Room : albums
 */
@Entity(
    tableName = "albums",
    foreignKeys = [
        ForeignKey(
            entity        = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns  = ["artist_id"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["normalized_title", "artist_id"], unique = true),
        Index(value = ["artist_id"]),
        Index(value = ["spotify_id"])
    ]
)
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "normalized_title")
    val normalizedTitle: String,

    @ColumnInfo(name = "artist_id")
    val artistId: Long,

    @ColumnInfo(name = "album_type")
    val albumType: String = AlbumType.ALBUM.name,

    @ColumnInfo(name = "release_date")
    val releaseDate: String? = null,

    @ColumnInfo(name = "total_tracks")
    val totalTracks: Int? = null,

    @ColumnInfo(name = "cover_url")
    val coverUrl: String? = null,

    @ColumnInfo(name = "spotify_id")
    val spotifyId: String? = null,

    @ColumnInfo(name = "musicbrainz_id")
    val musicBrainzId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Album = Album(
        id             = id,
        title          = title,
        normalizedTitle = normalizedTitle,
        artistId       = artistId,
        albumType      = AlbumType.valueOf(albumType),
        releaseDate    = releaseDate,
        totalTracks    = totalTracks,
        coverUrl       = coverUrl,
        spotifyId      = spotifyId,
        musicBrainzId  = musicBrainzId,
        createdAt      = createdAt,
        updatedAt      = updatedAt
    )

    companion object {
        fun fromDomain(album: Album): AlbumEntity = AlbumEntity(
            id              = album.id,
            title           = album.title,
            normalizedTitle = album.normalizedTitle,
            artistId        = album.artistId,
            albumType       = album.albumType.name,
            releaseDate     = album.releaseDate,
            totalTracks     = album.totalTracks,
            coverUrl        = album.coverUrl,
            spotifyId       = album.spotifyId,
            musicBrainzId   = album.musicBrainzId,
            createdAt       = album.createdAt,
            updatedAt       = album.updatedAt
        )
    }
}