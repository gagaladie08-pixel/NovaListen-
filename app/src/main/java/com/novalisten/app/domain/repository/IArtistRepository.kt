// domain/repository/IArtistRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.Artist
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Artist.
 */
interface IArtistRepository {

    suspend fun getArtistById(id: Long): Artist?

    suspend fun findArtistByName(normalizedName: String): Artist?

    suspend fun upsertArtist(artist: Artist): Long

    fun observeAllArtists(): Flow<List<Artist>>

    suspend fun searchArtists(query: String): List<Artist>
}