// domain/repository/IAlbumRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.Album
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Album.
 */
interface IAlbumRepository {

    suspend fun getAlbumById(id: Long): Album?

    suspend fun findAlbumByTitleAndArtist(
        normalizedTitle: String,
        artistId: Long
    ): Album?

    suspend fun upsertAlbum(album: Album): Long

    suspend fun getAlbumsByArtist(artistId: Long): List<Album>

    fun observeAlbumsByArtist(artistId: Long): Flow<List<Album>>

    suspend fun searchAlbums(query: String): List<Album>
}