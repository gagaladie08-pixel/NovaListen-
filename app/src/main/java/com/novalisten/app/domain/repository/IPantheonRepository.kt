// domain/repository/IPantheonRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.PantheonState
import com.novalisten.app.domain.model.enums.PantheonStatus
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Panthéon.
 * Artistes avec statut uniquement (les autres sont invisibles).
 */
interface IPantheonRepository {

    /** État Panthéon d'un artiste */
    suspend fun getPantheonState(artistId: Long): PantheonState?

    /** Sauvegarde ou met à jour un état Panthéon */
    suspend fun upsertPantheonState(state: PantheonState)

    /** Tous les artistes du Panthéon triés par statut DESC */
    fun observePantheon(): Flow<List<PantheonState>>

    /** Artistes par tier */
    suspend fun getArtistsByStatus(status: PantheonStatus): List<PantheonState>

    /** Artistes proches d'un nouveau statut (section "Bientôt…") */
    suspend fun getArtistsSoon(): List<PantheonState>
}