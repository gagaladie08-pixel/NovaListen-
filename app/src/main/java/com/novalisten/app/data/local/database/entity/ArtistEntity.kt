// data/local/database/entity/ArtistEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.novalisten.app.domain.model.entity.Artist

/**
 * Table Room : artists
 *
 * Index sur normalized_name pour matching rapide
 * lors de la détection musicale.
 */
@Entity(
    tableName = "artists",
    indices = [
        Index(value = ["normalized_name"], unique = true),
        Index(value = ["spotify_id"]),
        Index(value = ["musicbrainz_id"])
    ]
)
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "normalized_name")
    val normalizedName: String,

    @ColumnInfo(name = "genre")
    val genre: String? = null,

    @ColumnInfo(name = "country")
    val country: String? = null,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "bio_summary")
    val bioSummary: String? = null,

    @ColumnInfo(name = "spotify_id")
    val spotifyId: String? = null,

    @ColumnInfo(name = "musicbrainz_id")
    val musicBrainzId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Artist = Artist(
        id             = id,
        name           = name,
        normalizedName = normalizedName,
        genre          = genre,
        country        = country,
        imageUrl       = imageUrl,
        bioSummary     = bioSummary,
        spotifyId      = spotifyId,
        musicBrainzId  = musicBrainzId,
        createdAt      = createdAt,
        updatedAt      = updatedAt
    )

    companion object {
        fun fromDomain(artist: Artist): ArtistEntity = ArtistEntity(
            id             = artist.id,
            name           = artist.name,
            normalizedName = artist.normalizedName,
            genre          = artist.genre,
            country        = artist.country,
            imageUrl       = artist.imageUrl,
            bioSummary     = artist.bioSummary,
            spotifyId      = artist.spotifyId,
            musicBrainzId  = artist.musicBrainzId,
            createdAt      = artist.createdAt,
            updatedAt      = artist.updatedAt
        )
    }
}