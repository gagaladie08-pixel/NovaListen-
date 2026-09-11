// domain/model/entity/NovaAchievement.kt
package com.novalisten.app.domain.model.entity

import com.novalisten.app.domain.model.enums.AchievementType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.NovaAwardCategory
import com.novalisten.app.domain.model.enums.Period

/**
 * Status d'un Nova Award.
 * LIVE  → En cours (mis à jour en temps réel)
 * FINAL → Figé (cérémonie du 31 décembre passée)
 */
enum class AwardStatus { LIVE, FINAL }

/**
 * Réalisation NovaListen — Award ou Record.
 * Table unique nova_achievements, discriminée par type.
 */
data class NovaAchievement(
    val id: Long,
    val type: AchievementType,
    val year: Int,
    val entityType: EntityType?,
    val entityId: Long?,
    val awardCategory: NovaAwardCategory?,
    val awardStatus: AwardStatus?,
    val recordCategory: String?,
    val periodFilter: Period?,
    val valueNumeric: Double,
    val valueDisplay: String,
    val achievedAt: Long,
    val top10Json: String?
)