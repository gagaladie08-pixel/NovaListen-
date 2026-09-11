// domain/model/entity/PantheonState.kt
package com.novalisten.app.domain.model.entity

import com.novalisten.app.domain.model.enums.PantheonStatus

/**
 * État Panthéon d'un artiste.
 *
 * Deux chemins pour atteindre chaque statut :
 * - Option A : via les certifications (chansons + albums)
 * - Option B : via le total d'écoutes de l'artiste
 *
 * Le statut est PERMANENT (jamais perdu).
 * Les artistes sans statut sont invisibles dans le Panthéon.
 */
data class PantheonState(
    val artistId: Long,
    val status: PantheonStatus,
    val reachedAt: Long,
    val previousStatus: PantheonStatus?,

    // ── Compteurs de certifications chansons ──────────────────────────────
    val certifiedSongsSilver: Int,
    val certifiedSongsGold: Int,
    val certifiedSongsPlatinum: Int,
    val certifiedSongsDiamond: Int,

    // ── Compteurs de certifications albums ───────────────────────────────
    val certifiedAlbumsSilver: Int,
    val certifiedAlbumsGold: Int,
    val certifiedAlbumsPlatinum: Int,
    val certifiedAlbumsDiamond: Int,

    // ── Total écoutes (Option B) ──────────────────────────────────────────
    val totalPlays: Int,

    val updatedAt: Long
) {
    /**
     * Prochain statut à atteindre (null si déjà Mythique).
     */
    fun nextStatus(): PantheonStatus? = when (status) {
        PantheonStatus.STAR      -> PantheonStatus.SUPERSTAR
        PantheonStatus.SUPERSTAR -> PantheonStatus.MEGASTAR
        PantheonStatus.MEGASTAR  -> PantheonStatus.LEGENDE
        PantheonStatus.LEGENDE   -> PantheonStatus.MYTHIQUE
        PantheonStatus.MYTHIQUE  -> null
    }
}