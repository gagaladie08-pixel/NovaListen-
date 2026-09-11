// data/local/database/entity/BillboardEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table Room : billboard
 * Snapshots des classements + Hall of Fame intégré.
 *
 * Charts :
 *   NOVA_HOT_100   → Top 100 chansons (Daily: 75)
 *   NOVA_ARTIST_50 → Top 50 artistes  (Daily: 25)
 *   NOVA_75_ALBUMS → Top 75 albums    (Daily: 50)
 *
 * Mouvements :
 *   Daily   → vs hier
 *   Weekly  → vs semaine précédente
 *   Monthly → vs mois précédent
 *   Yearly  → vs année précédente
 *   Global  → vs semaine précédente
 *
 * Hall of Fame intégré via 4 colonnes booléennes.
 */
@Entity(
    tableName = "billboard",
    indices = [
        Index(
            value  = ["chart_type", "period_type", "period_key", "entity_id"],
            unique = true
        ),
        Index(value = ["chart_type", "period_type", "period_key", "rank"]),
        Index(value = ["entity_id", "chart_type"]),
        Index(value = ["hof_direct_debut"]),
        Index(value = ["hof_long_run"]),
        Index(value = ["hof_legendary_run"])
    ]
)
data class BillboardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "chart_type")
    val chartType: String,

    @ColumnInfo(name = "period_type")
    val periodType: String,

    @ColumnInfo(name = "period_key")
    val periodKey: String,

    @ColumnInfo(name = "entity_type")
    val entityType: String,

    @ColumnInfo(name = "entity_id")
    val entityId: Long,

    @ColumnInfo(name = "rank")
    val rank: Int,

    @ColumnInfo(name = "previous_rank")
    val previousRank: Int? = null,

    @ColumnInfo(name = "peak_rank")
    val peakRank: Int,

    @ColumnInfo(name = "weeks_on_chart")
    val weeksOnChart: Int = 1,

    @ColumnInfo(name = "movement")
    val movement: String,

    @ColumnInfo(name = "movement_value")
    val movementValue: Int = 0,

    @ColumnInfo(name = "play_count")
    val playCount: Int,

    @ColumnInfo(name = "listen_time_ms")
    val listenTimeMs: Long,

    @ColumnInfo(name = "calculated_at")
    val calculatedAt: Long = System.currentTimeMillis(),

    // ── Hall of Fame (intégré) ────────────────────────────────────────────
    @ColumnInfo(name = "hof_direct_debut")
    val hofDirectDebut: Boolean = false,

    @ColumnInfo(name = "hof_long_run")
    val hofLongRun: Boolean = false,

    @ColumnInfo(name = "hof_triple_debut")
    val hofTripleDebut: Boolean = false,

    @ColumnInfo(name = "hof_legendary_run")
    val hofLegendaryRun: Boolean = false,

    @ColumnInfo(name = "hof_entry_date")
    val hofEntryDate: Long? = null
)