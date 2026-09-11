// service/detection/PlaySaver.kt
package com.novalisten.app.service.detection

import com.novalisten.app.domain.model.entity.Artist
import com.novalisten.app.domain.model.entity.DetectionMethod
import com.novalisten.app.domain.model.entity.DeviceType
import com.novalisten.app.domain.model.entity.Play
import com.novalisten.app.domain.model.entity.PlaySource
import com.novalisten.app.domain.model.entity.Track
import com.novalisten.app.domain.repository.IArtistRepository
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.repository.ITrackRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sauvegarde une écoute validée en base de données.
 *
 * Responsabilités :
 * 1. Trouver ou créer l'artiste en BDD
 * 2. Trouver ou créer le morceau en BDD
 * 3. Créer et sauvegarder le Play
 *
 * C'est le dernier maillon du pipeline de détection.
 */
@Singleton
class PlaySaver @Inject constructor(
    private val playRepository: IPlayRepository,
    private val trackRepository: ITrackRepository,
    private val artistRepository: IArtistRepository,
    private val normalizer: TrackNormalizer,
    private val validator: TrackValidator,
    private val sessionManager: SessionManager
) {

    /**
     * Sauvegarde une écoute validée.
     *
     * @param event             Événement normalisé et validé
     * @param durationListenedMs Durée réellement écoutée
     * @param startedAt         Timestamp de début d'écoute
     * @return ID de l'écoute sauvegardée, ou -1 si échec
     */
    suspend fun save(
        event: RawTrackEvent,
        durationListenedMs: Long,
        startedAt: Long
    ): Long {
        return try {
            // 1. Trouver ou créer l'artiste
            val artistId = findOrCreateArtist(event.artist)

            // 2. Trouver ou créer le morceau
            val trackId = findOrCreateTrack(event, artistId)

            // 3. Calculer le taux de complétion
            val completionRate = validator.completionRate(
                durationListenedMs, event.durationMs
            )

            // 4. Créer et sauvegarder le Play
            val play = Play(
                id               = 0,
                trackId          = trackId,
                artistId         = artistId,
                albumId          = null,
                startedAt        = startedAt,
                endedAt          = System.currentTimeMillis(),
                durationListened = durationListenedMs,
                completionRate   = completionRate,
                source           = event.source,
                detectionMethod  = event.detectionMethod,
                isValid          = true,
                sessionId        = sessionManager.getCurrentSessionId(),
                deviceType       = DeviceType.PHONE
            )

            playRepository.savePlay(play)

        } catch (e: Exception) {
            -1L
        }
    }

    // ── Helpers privés ────────────────────────────────────────────────────

    private suspend fun findOrCreateArtist(name: String): Long {
        val normalizedName = normalizer.toNormalizedKey(name)

        // Cherche l'artiste existant
        val existing = artistRepository.findArtistByName(normalizedName)
        if (existing != null) return existing.id

        // Crée un nouvel artiste minimal
        val newArtist = Artist(
            id             = 0,
            name           = name.trim(),
            normalizedName = normalizedName,
            genre          = null,
            country        = null,
            imageUrl       = null,
            bioSummary     = null,
            spotifyId      = null,
            musicBrainzId  = null,
            createdAt      = System.currentTimeMillis(),
            updatedAt      = System.currentTimeMillis()
        )
        return artistRepository.upsertArtist(newArtist)
    }

    private suspend fun findOrCreateTrack(
        event: RawTrackEvent,
        artistId: Long
    ): Long {
        val normalizedTitle = normalizer.toNormalizedKey(event.title)

        // Cherche le morceau existant
        val existing = trackRepository.findTrackByTitleAndArtist(
            normalizedTitle, artistId
        )
        if (existing != null) return existing.id

        // Crée un nouveau morceau minimal
        val newTrack = Track(
            id              = 0,
            title           = event.title.trim(),
            normalizedTitle = normalizedTitle,
            artistId        = artistId,
            albumId         = null,
            durationMs      = event.durationMs,
            genre           = null,
            releaseDate     = null,
            isExplicit      = false,
            spotifyId       = null,
            musicBrainzId   = null,
            isrc            = null,
            createdAt       = System.currentTimeMillis(),
            updatedAt       = System.currentTimeMillis()
        )
        return trackRepository.upsertTrack(newTrack)
    }
}