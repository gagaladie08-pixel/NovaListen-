// data/repository/PantheonRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.PantheonDao
import com.novalisten.app.data.local.database.entity.PantheonEntity
import com.novalisten.app.domain.model.entity.PantheonState
import com.novalisten.app.domain.model.enums.PantheonStatus
import com.novalisten.app.domain.repository.IPantheonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PantheonRepositoryImpl @Inject constructor(
    private val pantheonDao: PantheonDao
) : IPantheonRepository {

    override suspend fun getPantheonState(artistId: Long): PantheonState? =
        pantheonDao.getByArtist(artistId)?.toDomain()

    override suspend fun upsertPantheonState(state: PantheonState) =
        pantheonDao.upsert(state.toEntity())

    override fun observePantheon(): Flow<List<PantheonState>> =
        pantheonDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun getArtistsByStatus(
        status: PantheonStatus
    ): List<PantheonState> =
        pantheonDao.getByStatus(status.name).map { it.toDomain() }

    override suspend fun getArtistsSoon(): List<PantheonState> =
        pantheonDao.getSoon().map { it.toDomain() }

    // ── Mappers ────────────────────────────────────────────────────────────

    private fun PantheonEntity.toDomain(): PantheonState = PantheonState(
        artistId               = artistId,
        status                 = PantheonStatus.valueOf(status),
        reachedAt              = reachedAt,
        previousStatus         = previousStatus?.let { PantheonStatus.valueOf(it) },
        certifiedSongsSilver   = certifiedSongsSilver,
        certifiedSongsGold     = certifiedSongsGold,
        certifiedSongsPlatinum = certifiedSongsPlatinum,
        certifiedSongsDiamond  = certifiedSongsDiamond,
        certifiedAlbumsSilver  = certifiedAlbumsSilver,
        certifiedAlbumsGold    = certifiedAlbumsGold,
        certifiedAlbumsPlatinum = certifiedAlbumsPlatinum,
        certifiedAlbumsDiamond = certifiedAlbumsDiamond,
        totalPlays             = totalPlays,
        updatedAt              = updatedAt
    )

    private fun PantheonState.toEntity(): PantheonEntity = PantheonEntity(
        artistId               = artistId,
        status                 = status.name,
        reachedAt              = reachedAt,
        previousStatus         = previousStatus?.name,
        certifiedSongsSilver   = certifiedSongsSilver,
        certifiedSongsGold     = certifiedSongsGold,
        certifiedSongsPlatinum = certifiedSongsPlatinum,
        certifiedSongsDiamond  = certifiedSongsDiamond,
        certifiedAlbumsSilver  = certifiedAlbumsSilver,
        certifiedAlbumsGold    = certifiedAlbumsGold,
        certifiedAlbumsPlatinum = certifiedAlbumsPlatinum,
        certifiedAlbumsDiamond = certifiedAlbumsDiamond,
        totalPlays             = totalPlays,
        updatedAt              = updatedAt
    )
}