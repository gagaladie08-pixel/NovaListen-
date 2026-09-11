// data/local/database/entity/NovaAchievementEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table Room : nova_achievements
 * Awards + Records — table unifiée discriminée par type.
 *
 * type = AWARD  → Nova Awards (9 catégories)
 * type = RECORD → 24 records personnels
 *
 * Déblocage Awards : 2 mois d'utilisation minimum.
 * Cérémonie : 31 décembre (LIVE → FINAL).
 */
@Entity(
    tableName = "nova_achievements",
    indices = [
        Index(value = ["type", "year"]),
        Index(value = ["award_category", "year"]),
        Index(value = ["record_category", "period_filter"]),
        Index(value = ["entity_type", "entity_id"])
    ]
)
data class NovaAchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "type")
    val type: String,                       // AWARD / RECORD

    @ColumnInfo(name = "year")
    val year: Int,

    @ColumnInfo(name = "entity_type")
    val entityType: String? = null,

    @ColumnInfo(name = "entity_id")
    val entityId: Long? = null,

    // ── Awards ────────────────────────────────────────────────────────────
    @ColumnInfo(name = "award_category")
    val awardCategory: String? = null,

    @ColumnInfo(name = "award_status")
    val awardStatus: String? = null,        // LIVE / FINAL

    // ── Records ───────────────────────────────────────────────────────────
    @ColumnInfo(name = "record_category")
    val recordCategory: String? = null,

    @ColumnInfo(name = "period_filter")
    val periodFilter: String? = null,

    // ── Valeur commune ────────────────────────────────────────────────────
    @ColumnInfo(name = "value_numeric")
    val valueNumeric: Double,

    @ColumnInfo(name = "value_display")
    val valueDisplay: String,

    @ColumnInfo(name = "achieved_at")
    val achievedAt: Long,

    @ColumnInfo(name = "top10_json")
    val top10Json: String? = null
)