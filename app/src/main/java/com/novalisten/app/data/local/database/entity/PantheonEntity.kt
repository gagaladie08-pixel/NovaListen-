// data/local/database/entity/PantheonEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table Room : pantheon
 * Statuts artistes NovaListen.
 *
 * Statuts (ordre croissant) :
 * STAR → SUPERSTAR → MEGASTAR → LEGENDE → MYTHIQUE
 *
 * Règle : statut PERMANENT (jamais perdu).
 * Artistes sans statut = invisibles dans le Panthéon.
 */
@Entity(
    tableName = "pantheon",
    foreignKeys = [
        ForeignKey(
            entity        = ArtistEntity::class,
            parentColumns = ["id"],
            childColumns  = ["artist_id"],
            onDelete      = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["artist_id"], unique = true),
        Index(value = ["status"])
    ]
)
data class PantheonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "artist_id")
    val artistId: Long,

    @ColumnInfo(name = "status")
    val status: String,               // STAR / SUPERSTAR / MEGASTAR / LEGENDE / MYTHIQUE

    @ColumnInfo(name = "reached_at")
    val reachedAt: Long,

    @ColumnInfo(name = "previous_status")
    val previousStatus: String? = null,

    // ── Compteurs certifications chansons ─────────────────────────────────
    @ColumnInfo(name = "certified_songs_silver")
    val certifiedSongsSilver: Int = 0,

    @ColumnInfo(name = "certified_songs_gold")
    val certifiedSongsGold: Int = 0,

    @ColumnInfo(name = "certified_songs_platinum")
    val certifiedSongsPlatinum: Int = 0,

    @ColumnInfo(name = "certified_songs_diamond")
    val certifiedSongsDiamond: Int = 0,

    // ── Compteurs certifications albums ───────────────────────────────────
    @ColumnInfo(name = "certified_albums_silver")
    val certifiedAlbumsSilver: Int = 0,

    @ColumnInfo(name = "certified_albums_gold")
    val certifiedAlbumsGold: Int = 0,

    @ColumnInfo(name = "certified_albums_platinum")
    val certifiedAlbumsPlatinum: Int = 0,

    @ColumnInfo(name = "certified_albums_diamond")
    val certifiedAlbumsDiamond: Int = 0,

    // ── Total écoutes artiste (Option B) ──────────────────────────────────
    @ColumnInfo(name = "total_plays")
    val totalPlays: Int = 0,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)