// domain/repository/ITrackRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.Track
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Track.
 * Le Domain ne sait pas si les données viennent de Room ou d'une API.
 */
interface ITrackRepository {

    /** Récupère un morceau par son ID */
    suspend fun getTrackById(id: Long): Track?

    /** Cherche un morceau par titre normalisé + artiste */
    suspend fun findTrackByTitleAndArtist(
        normalizedTitle: String,
        artistId: Long
    ): Track?

    /** Insère ou met à jour un morceau */
    suspend fun upsertTrack(track: Track): Long

    /** Flux de tous les morceaux (observé en temps réel) */
    fun observeAllTracks(): Flow<List<Track>>

    /** Cherche des morceaux par titre (recherche dans l'UI) */
    suspend fun searchTracks(query: String): List<Track>
}