// data/repository/TrackRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.TrackDao
import com.novalisten.app.data.local.database.entity.TrackEntity
import com.novalisten.app.domain.model.entity.Track
import com.novalisten.app.domain.repository.ITrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TrackRepositoryImpl @Inject constructor(
    private val trackDao: TrackDao
) : ITrackRepository {

    override suspend fun getTrackById(id: Long): Track? =
        trackDao.getById(id)?.toDomain()

    override suspend fun findTrackByTitleAndArtist(
        normalizedTitle: String,
        artistId: Long
    ): Track? = trackDao.findByTitleAndArtist(normalizedTitle, artistId)?.toDomain()

    override suspend fun upsertTrack(track: Track): Long {
        val entity = TrackEntity.fromDomain(track)
        val existingId = trackDao.insert(entity)
        return if (existingId == -1L) {
            trackDao.update(entity)
            track.id
        } else {
            existingId
        }
    }

    override fun observeAllTracks(): Flow<List<Track>> =
        trackDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun searchTracks(query: String): List<Track> =
        trackDao.search(query).map { it.toDomain() }
}