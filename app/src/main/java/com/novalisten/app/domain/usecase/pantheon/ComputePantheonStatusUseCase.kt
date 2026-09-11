// domain/usecase/pantheon/ComputePantheonStatusUseCase.kt
package com.novalisten.app.domain.usecase.pantheon

import com.novalisten.app.core.common.Result
import com.novalisten.app.domain.model.entity.PantheonState
import com.novalisten.app.domain.model.enums.CertificationLevel
import com.novalisten.app.domain.model.enums.PantheonStatus
import com.novalisten.app.domain.repository.ICertificationRepository
import com.novalisten.app.domain.repository.IPantheonRepository
import com.novalisten.app.domain.repository.IPlayRepository
import javax.inject.Inject

/**
 * Calcule et met à jour le statut Panthéon d'un artiste.
 *
 * Appelé après chaque nouvelle certification
 * ou après chaque écoute valide (pour Option B).
 *
 * 2 chemins pour chaque statut :
 *
 * STAR :
 *   A: 5+ chansons Argent+ ET 2+ albums Argent+
 *   B: 425 écoutes totales
 *
 * SUPERSTAR :
 *   A: 4+ chansons Or+ ET 2+ albums Or+
 *   B: 650 écoutes totales
 *
 * MEGASTAR :
 *   A: 5+ chansons Platine+ ET 2+ albums Platine+
 *   B: 1250 écoutes totales
 *
 * LÉGENDE :
 *   A: 5+ chansons Diamant ET 2+ albums Diamant
 *   B: 3650 écoutes totales
 *
 * MYTHIQUE :
 *   A: Légende + 2 titres à CHAQUE niveau + 2 albums à CHAQUE niveau
 *   B: 7000 écoutes totales
 *
 * Statut PERMANENT — jamais perdu.
 */
class ComputePantheonStatusUseCase @Inject constructor(
    private val pantheonRepository: IPantheonRepository,
    private val certificationRepository: ICertificationRepository,
    private val playRepository: IPlayRepository
) {

    suspend operator fun invoke(artistId: Long): Result<PantheonState?> {
        return try {
            // 1. Total écoutes de l'artiste (Option B)
            val totalPlays = playRepository.getTotalValidPlaysForArtist(artistId)

            // 2. Compteurs de certifications par niveau
            val songsSilver   = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.SONG,
                    CertificationLevel.SILVER
                ).count { isSongOfArtist(it.entityId, artistId) }

            val songsGold     = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.SONG,
                    CertificationLevel.GOLD
                ).count { isSongOfArtist(it.entityId, artistId) }

            val songsPlatinum = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.SONG,
                    CertificationLevel.PLATINUM
                ).count { isSongOfArtist(it.entityId, artistId) }

            val songsDiamond  = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.SONG,
                    CertificationLevel.DIAMOND
                ).count { isSongOfArtist(it.entityId, artistId) }

            val albumsSilver   = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.ALBUM,
                    CertificationLevel.SILVER
                ).count { isAlbumOfArtist(it.entityId, artistId) }

            val albumsGold     = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.ALBUM,
                    CertificationLevel.GOLD
                ).count { isAlbumOfArtist(it.entityId, artistId) }

            val albumsPlatinum = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.ALBUM,
                    CertificationLevel.PLATINUM
                ).count { isAlbumOfArtist(it.entityId, artistId) }

            val albumsDiamond  = certificationRepository
                .getEntitiesByLevel(
                    com.novalisten.app.domain.model.enums.EntityType.ALBUM,
                    CertificationLevel.DIAMOND
                ).count { isAlbumOfArtist(it.entityId, artistId) }

            // 3. Détermine le statut le plus élevé atteint
            val newStatus = computeHighestStatus(
                totalPlays     = totalPlays,
                songsSilver    = songsSilver,
                songsGold      = songsGold,
                songsPlatinum  = songsPlatinum,
                songsDiamond   = songsDiamond,
                albumsSilver   = albumsSilver,
                albumsGold     = albumsGold,
                albumsPlatinum = albumsPlatinum,
                albumsDiamond  = albumsDiamond
            )

            // 4. Aucun statut → pas dans le Panthéon
            if (newStatus == null) return Result.Success(null)

            // 5. Récupère l'état actuel
            val existing = pantheonRepository.getPantheonState(artistId)

            // 6. Statut permanent → on ne rétrograde jamais
            val finalStatus = if (existing != null &&
                existing.status.rank() >= newStatus.rank()) {
                existing.status
            } else {
                newStatus
            }

            // 7. Pas de changement → retourne l'existant
            if (existing?.status == finalStatus) {
                return Result.Success(existing)
            }

            // 8. Sauvegarde le nouveau statut
            val newState = PantheonState(
                artistId               = artistId,
                status                 = finalStatus,
                reachedAt              = System.currentTimeMillis(),
                previousStatus         = existing?.status,
                certifiedSongsSilver   = songsSilver,
                certifiedSongsGold     = songsGold,
                certifiedSongsPlatinum = songsPlatinum,
                certifiedSongsDiamond  = songsDiamond,
                certifiedAlbumsSilver  = albumsSilver,
                certifiedAlbumsGold    = albumsGold,
                certifiedAlbumsPlatinum = albumsPlatinum,
                certifiedAlbumsDiamond = albumsDiamond,
                totalPlays             = totalPlays,
                updatedAt              = System.currentTimeMillis()
            )

            pantheonRepository.upsertPantheonState(newState)
            Result.Success(newState)

        } catch (e: Exception) {
            Result.Error("Erreur calcul Panthéon : ${e.message}", e)
        }
    }

    // ── Logique de calcul du statut ───────────────────────────────────────

    private fun computeHighestStatus(
        totalPlays: Int,
        songsSilver: Int, songsGold: Int,
        songsPlatinum: Int, songsDiamond: Int,
        albumsSilver: Int, albumsGold: Int,
        albumsPlatinum: Int, albumsDiamond: Int
    ): PantheonStatus? {

        // MYTHIQUE
        val isMythiqueA = songsDiamond >= 2 && songsGold >= 2 &&
                songsSilver >= 2 && songsPlatinum >= 2 &&
                albumsDiamond >= 2 && albumsGold >= 2 &&
                albumsSilver >= 2 && albumsPlatinum >= 2
        val isMythiqueB = totalPlays >= 7000
        if (isMythiqueA || isMythiqueB) return PantheonStatus.MYTHIQUE

        // LÉGENDE
        val isLegendeA = songsDiamond >= 5 && albumsDiamond >= 2
        val isLegendeB = totalPlays >= 3650
        if (isLegendeA || isLegendeB) return PantheonStatus.LEGENDE

        // MEGASTAR
        val isMegastarA = (songsPlatinum + songsDiamond) >= 5 &&
                (albumsPlatinum + albumsDiamond) >= 2
        val isMegastarB = totalPlays >= 1250
        if (isMegastarA || isMegastarB) return PantheonStatus.MEGASTAR

        // SUPERSTAR
        val isSuperstarA = (songsGold + songsPlatinum + songsDiamond) >= 4 &&
                (albumsGold + albumsPlatinum + albumsDiamond) >= 2
        val isSuperstarB = totalPlays >= 650
        if (isSuperstarA || isSuperstarB) return PantheonStatus.SUPERSTAR

        // STAR
        val isStarA = (songsSilver + songsGold + songsPlatinum + songsDiamond) >= 5 &&
                (albumsSilver + albumsGold + albumsPlatinum + albumsDiamond) >= 2
        val isStarB = totalPlays >= 425
        if (isStarA || isStarB) return PantheonStatus.STAR

        return null
    }

    // ── Helpers (à enrichir avec les vrais repos Track/Album) ─────────────

    private fun isSongOfArtist(songId: Long, artistId: Long): Boolean = true
    private fun isAlbumOfArtist(albumId: Long, artistId: Long): Boolean = true
}