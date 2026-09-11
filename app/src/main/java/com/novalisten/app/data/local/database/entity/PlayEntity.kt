// data/local/database/entity/PlayEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.novalisten.app.domain.model.entity.DetectionMethod
import com.novalisten.app.domain.model.entity.DeviceType
import com.novalisten.app.domain.model.entity.Play
import com.novalisten.app.domain.model.entity.PlaySource

/**
 * Table Room : plays — LE CŒUR de NovaListen.
 *
 * Index stratégiques pour performance :
 * → (track_id, started_at)  : stats par morceau
 * → (artist_id, started_at) : stats par artiste
 * → (is_valid, started_at)  : toutes les requêtes filtrent sur is_valid
 * → (session_id)            : grouper les écoutes d'une session
 */
@Entity(
    tableName = "plays",
    foreignKeys = [
        ForeignKey(
            entity        = TrackEntity::class,
            parentColumns = ["id"],
            childColumns  = ["track_id"],
            onDelete      = ForeignKey.CASCADE
        ),
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
        Index(value = ["track_id",  "started_at"]),
        Index(value = ["artist_id", "started_at"]),
        Index(value = ["album_id",  "started_at"]),
        Index(value = ["is_valid",  "started_at"]),
        Index(value = ["session_id"])
    ]
)
data class PlayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "track_id")
    val trackId: Long,

    @ColumnInfo(name = "artist_id")
    val artistId: Long,

    @ColumnInfo(name = "album_id")
    val albumId: Long? = null,

    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    @ColumnInfo(name = "ended_at")
    val endedAt: Long? = null,

    @ColumnInfo(name = "duration_listened")
    val durationListened: Long,

    @ColumnInfo(name = "completion_rate")
    val completionRate: Float,

    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "detection_method")
    val detectionMethod: String,

    @ColumnInfo(name = "is_valid")
    val isValid: Boolean,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "device_type")
    val deviceType: String
) {
    fun toDomain(): Play = Play(
        id              = id,
        trackId         = trackId,
        artistId        = artistId,
        albumId         = albumId,
        startedAt       = startedAt,
        endedAt         = endedAt,
        durationListened = durationListened,
        completionRate  = completionRate,
        source          = PlaySource.valueOf(source),
        detectionMethod = DetectionMethod.valueOf(detectionMethod),
        isValid         = isValid,
        sessionId       = sessionId,
        deviceType      = DeviceType.valueOf(deviceType)
    )

    companion object {
        fun fromDomain(play: Play): PlayEntity = PlayEntity(
            id               = play.id,
            trackId          = play.trackId,
            artistId         = play.artistId,
            albumId          = play.albumId,
            startedAt        = play.startedAt,
            endedAt          = play.endedAt,
            durationListened = play.durationListened,
            completionRate   = play.completionRate,
            source           = play.source.name,
            detectionMethod  = play.detectionMethod.name,
            isValid          = play.isValid,
            sessionId        = play.sessionId,
            deviceType       = play.deviceType.name
        )
    }
}