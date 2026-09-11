// domain/model/entity/CertificationState.kt
package com.novalisten.app.domain.model.entity

import com.novalisten.app.domain.model.enums.CertificationLevel
import com.novalisten.app.domain.model.enums.EntityType

/**
 * État de certification d'un morceau ou album.
 *
 * diamondMultiplier :
 *   1 = 💎 Diamant simple
 *   2 = 💎💎 Double Diamant
 *   3 = 💎💎💎 Triple Diamant
 *   etc. (infini)
 *
 * Affichage Diamant :
 *   if level == DIAMOND → répète l'emoji diamondMultiplier fois
 *
 * nextThresholdDelta :
 *   Nombre d'écoutes manquantes pour le prochain palier.
 *   Utilisé dans le Radar "Bientôt certifié".
 */
data class CertificationState(
    val id: Long,
    val entityType: EntityType,       // SONG ou ALBUM uniquement
    val entityId: Long,
    val level: CertificationLevel,
    val diamondMultiplier: Int,       // 1 par défaut, augmente à chaque palier Diamant
    val playCountAtCert: Int,
    val certifiedAt: Long,
    val nextThresholdDelta: Int,      // écoutes manquantes pour prochain palier
    val history: List<CertificationHistoryEntry>
)

/**
 * Entrée dans l'historique de certification.
 * Chaque palier franchi est daté.
 */
data class CertificationHistoryEntry(
    val level: CertificationLevel,
    val diamondMultiplier: Int,
    val playCountAtTime: Int,
    val certifiedAt: Long
)