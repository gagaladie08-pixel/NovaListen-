// data/repository/ArtistRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.ArtistDao
import com.novalisten.app.data.local.database.entity.ArtistEntity
import com.novalisten.app.domain.model.entity.Artist
import com.novalisten.app.domain.repository.IArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    private val artistDao: ArtistDao
) : IArtistRepository {

    override suspend fun getArtistById(id: Long): Artist? =
        artistDao.getById(id)?.toDomain()

    override suspend fun findArtistByName(normalizedName: String): Artist? =
        artistDao.findByName(normalizedName)?.toDomain()

    override suspend fun upsertArtist(artist: Artist): Long {
        val entity = ArtistEntity.fromDomain(artist)
        val existingId = artistDao.insert(entity)
        return if (existingId == -1L) {
            artistDao.update(entity)
            artist.id
        } else {
            existingId
        }
    }

    override fun observeAllArtists(): Flow<List<Artist>> =
        artistDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun searchArtists(query: String): List<Artist> =
        artistDao.search(query).map { it.toDomain() }
}