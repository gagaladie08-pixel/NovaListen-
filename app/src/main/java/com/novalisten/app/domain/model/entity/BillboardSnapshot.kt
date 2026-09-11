// domain/model/entity/BillboardSnapshot.kt
package com.novalisten.app.domain.model.entity

import com.novalisten.app.domain.model.enums.ChartType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Movement
import com.novalisten.app.domain.model.enums.Period

/**
 * Un snapshot du Billboard à un instant donné.
 * Sauvegardé à la fin de chaque période calendaire.
 */
data class BillboardSnapshot(
    val chartType: ChartType,
    val period: Period,
    val periodKey: String,       // Ex: "2026-W37" / "2026-09" / "2026" / "2026-09-11"
    val calculatedAt: Long,
    val entries: List<BillboardEntry>
)

/**
 * Une entrée dans le Billboard.
 * Contient les infos de classement + Hall of Fame intégré.
 */
data class BillboardEntry(
    val id: Long,
    val entityType: EntityType,
    val entityId: Long,
    val rank: Int,
    val previousRank: Int?,
    val peakRank: Int,
    val weeksOnChart: Int,
    val movement: Movement,
    val movementValue: Int,
    val playCount: Int,
    val listenTimeMs: Long,

    // ── Hall of Fame (intégré au Billboard) ──────────────────────────────
    val hofDirectDebut: Boolean,      // Entrée directe #1 Weekly/Monthly
    val hofLongRun: Boolean,          // 3 semaines #1 Weekly / 2 mois #1 Monthly
    val hofTripleDebut: Boolean,      // #1 Daily + Weekly + Monthly simultanément
    val hofLegendaryRun: Boolean,     // 10× #1 Weekly non consécutives
    val hofEntryDate: Long?           // Date entrée Hall of Fame
)