// data/local/database/entity/CertificationEntity.kt
package com.novalisten.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table Room : certifications
 * Chansons et Albums uniquement (pas d'artistes).
 *
 * Seuils Chansons :
 *   Argent  →   25 | Or → 50 | Platine → 100
 *   Diamant → 350 (+350 × diamond_multiplier)
 *
 * Seuils Albums :
 *   Argent  →   50 | Or → 100 | Platine → 200
 *   Diamant → 700 (+700 × diamond_multiplier)
 *
 * Écoutes album = somme des écoutes de tous ses titres.
 */
@Entity(
    tableName = "certifications",
    indices = [
        Index(
            value  = ["entity_type", "entity_id"],
            unique = true
        ),
        Index(value = ["level", "entity_type"])
    ]
)
data class CertificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "entity_type")
    val entityType: String,           // SONG ou ALBUM

    @ColumnInfo(name = "entity_id")
    val entityId: Long,

    @ColumnInfo(name = "level")
    val level: String,                // SILVER / GOLD / PLATINUM / DIAMOND

    @ColumnInfo(name = "diamond_multiplier")
    val diamondMultiplier: Int = 1,   // 1 = simple, 2 = double, etc.

    @ColumnInfo(name = "play_count_at_cert")
    val playCountAtCert: Int,

    @ColumnInfo(name = "certified_at")
    val certifiedAt: Long,

    @ColumnInfo(name = "next_threshold_delta")
    val nextThresholdDelta: Int = 0,  // écoutes manquantes pour prochain palier

    @ColumnInfo(name = "history_json")
    val historyJson: String = "[]"    // JSON de l'historique des paliers
)