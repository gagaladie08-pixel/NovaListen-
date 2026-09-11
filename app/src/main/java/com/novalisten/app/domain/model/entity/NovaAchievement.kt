// domain/model/entity/NovaAchievement.kt
package com.novalisten.app.domain.model.entity

import com.novalisten.app.domain.model.enums.AchievementType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.NovaAwardCategory
import com.novalisten.app.domain.model.enums.Period

/**
 * Status d'un Nova Award.
 * LIVE   → En cours (mis à jour en temps réel)
 * FINAL  → Figé (cérémonie du 31 décembre passée)
 */
enum class AwardStatus { LIVE, FINAL }

/**
 * Réalisation NovaListen — Award ou Record.
 * Table unique nova_achievements, discriminée par type.
 */
data class NovaAchievement(
    val id: Long,
    val type: AchievementType,              // AWARD ou RECORD
    val year: Int,
    val entityType: EntityType?,            // nullable (records globaux)
    val entityId: Long?,                    // nullable

    // ── Champs Awards ─────────────────────────────────────────────────────
    val awardCategory: NovaAwardCategory?,  // null si type == RECORD
    val awardStatus: AwardStatus?,          // LIVE ou FINAL

    // ── Champs Records ────────────────────────────────────────────────────
    val recordCategory: String?,            // Ex: "MOST_CUMULATIVE"
    val periodFilter: Period?,              // Pour records multi-périodes

    // ── Valeur commune ────────────────────────────────────────────────────
    val valueNumeric: Double,               // Pour tri et comparaison
    val valueDisplay: String,               // Ex: "47 écoutes" / "3h 22min"
    val achievedAt: Long,

    // ── Top 10 (pour les records) ─────────────────────────────────────────
    val top10Json: String?                  // JSON du top 10 sérialisé
)