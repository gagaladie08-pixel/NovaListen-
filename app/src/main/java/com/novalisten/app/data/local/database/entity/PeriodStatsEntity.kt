// data/local/database/entity/PeriodStatsEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table Room : period_stats
 * Agrégats pré-calculés pour la performance.
 * Remplace 4 tables (daily/weekly/monthly/yearly).
 *
 * period_type : DAILY / WEEKLY / MONTHLY / YEARLY
 * period_key  :
 *   DAILY   → "2026-09-11"
 *   WEEKLY  → "2026-W37"
 *   MONTHLY → "2026-09"
 *   YEARLY  → "2026"
 *
 * entity_type : SONG / ARTIST / ALBUM
 *
 * Index sur (entity_type, entity_id, period_type, period_key)
 * pour toutes les requêtes de classement.
 */
@Entity(
    tableName = "period_stats",
    indices = [
        Index(
            value  = ["entity_type", "entity_id", "period_type", "period_key"],
            unique = true
        ),
        Index(value = ["period_type", "period_key", "entity_type", "play_count"])
    ]
)
data class PeriodStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "period_type")
    val periodType: String,

    @ColumnInfo(name = "period_key")
    val periodKey: String,

    @ColumnInfo(name = "entity_type")
    val entityType: String,

    @ColumnInfo(name = "entity_id")
    val entityId: Long,

    @ColumnInfo(name = "play_count")
    val playCount: Int,

    @ColumnInfo(name = "listen_time_ms")
    val listenTimeMs: Long,

    @ColumnInfo(name = "rank")
    val rank: Int = 0
)