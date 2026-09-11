// data/repository/AlbumRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.AlbumDao
import com.novalisten.app.data.local.database.entity.AlbumEntity
import com.novalisten.app.domain.model.entity.Album
import com.novalisten.app.domain.repository.IAlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumDao: AlbumDao
) : IAlbumRepository {

    override suspend fun getAlbumById(id: Long): Album? =
        albumDao.getById(id)?.toDomain()

    override suspend fun findAlbumByTitleAndArtist(
        normalizedTitle: String,
        artistId: Long
    ): Album? = albumDao.findByTitleAndArtist(normalizedTitle, artistId)?.toDomain()

    override suspend fun upsertAlbum(album: Album): Long {
        val entity = AlbumEntity.fromDomain(album)
        val existingId = albumDao.insert(entity)
        return if (existingId == -1L) {
            albumDao.update(entity)
            album.id
        } else {
            existingId
        }
    }

    override suspend fun getAlbumsByArtist(artistId: Long): List<Album> =
        albumDao.getByArtist(artistId).map { it.toDomain() }

    override fun observeAlbumsByArtist(artistId: Long): Flow<List<Album>> =
        albumDao.observeByArtist(artistId).map { list -> list.map { it.toDomain() } }

    override suspend fun searchAlbums(query: String): List<Album> =
        albumDao.search(query).map { it.toDomain() }
}