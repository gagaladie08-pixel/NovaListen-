// domain/model/entity/RankedItem.kt
package com.novalisten.app.domain.model.entity

import com.novalisten.app.domain.model.enums.EntityType

/**
 * Résultat d'un classement (Stats ou Billboard).
 *
 * Règle de tri :
 * → Tri principal  : playCount DESC
 * → Tri secondaire : listenTimeMs DESC (départage égalités)
 *
 * Position affichée :
 * → 1-3  : 🥇🥈🥉 (podium)
 * → 4-10 : 🔥 (top 10)
 * → 11-300 : position normale
 * → > 300  : ▼ 300+
 * → Absent : —
 */
data class RankedItem(
    val position: Int,
    val entityId: Long,
    val entityType: EntityType,
    val playCount: Int,
    val listenTimeMs: Long,
    val certificationLevel: CertificationState? = null,
    val pantheonStatus: PantheonState? = null
) {
    /** Emoji de position pour l'UI */
    fun positionEmoji(): String = when (position) {
        1    -> "🥇"
        2    -> "🥈"
        3    -> "🥉"
        in 4..10 -> "🔥"
        else -> "$position"
    }

    /** Formatage du temps d'écoute */
    fun listenTimeFormatted(): String {
        val hours   = listenTimeMs / 3_600_000
        val minutes = (listenTimeMs % 3_600_000) / 60_000
        return when {
            hours > 0   -> "${hours}h ${minutes}min"
            minutes > 0 -> "${minutes}min"
            else        -> "${listenTimeMs / 1_000}s"
        }
    }
}