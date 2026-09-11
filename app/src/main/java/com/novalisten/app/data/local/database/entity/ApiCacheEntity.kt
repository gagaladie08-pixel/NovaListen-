// data/local/database/entity/ApiCacheEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table Room : api_cache
 * Cache des réponses API + log de synchronisation.
 * Fusionne api_cache + sync_log en une seule table.
 *
 * TTL par source :
 *   Spotify track metadata → 30 jours
 *   Spotify artist         →  7 jours
 *   Last.fm artist         → 14 jours
 *   Last.fm tags           → 30 jours
 *   MusicBrainz            → 90 jours
 *   Fanart images          → 60 jours
 */
@Entity(
    tableName = "api_cache",
    indices = [
        Index(
            value  = ["api_source", "endpoint_key"],
            unique = true
        ),
        Index(value = ["api_source", "expires_at"]),
        Index(value = ["sync_status"])
    ]
)
data class ApiCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "api_source")
    val apiSource: String,              // SPOTIFY / LASTFM / FANART / MUSICBRAINZ

    @ColumnInfo(name = "endpoint_key")
    val endpointKey: String,            // URL ou clé unique de requête

    @ColumnInfo(name = "response_json")
    val responseJson: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long,

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "SUCCESS", // SUCCESS / FAILED / PENDING

    @ColumnInfo(name = "sync_error")
    val syncError: String? = null,

    @ColumnInfo(name = "hit_count")
    val hitCount: Int = 0
)