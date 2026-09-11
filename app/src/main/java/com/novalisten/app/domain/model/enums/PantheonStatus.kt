// domain/model/enums/PantheonStatus.kt
package com.novalisten.app.domain.model.enums

/**
 * Les 5 statuts du Panthéon NovaListen.
 *
 * Chaque statut est atteint via OPTION A (certifications) OU OPTION B (total écoutes).
 *
 * ⭐ STAR
 *   A: 5+ chansons Argent+ ET 2+ albums Argent+
 *   B: 425 écoutes totales artiste
 *
 * 🌟 SUPERSTAR
 *   A: 4+ chansons Or+ ET 2+ albums Or+
 *   B: 650 écoutes totales artiste
 *
 * 👑 MEGASTAR
 *   A: 5+ chansons Platine+ ET 2+ albums Platine+
 *   B: 1250 écoutes totales artiste
 *
 * 🏛️ LÉGENDE
 *   A: 5+ chansons Diamant ET 2+ albums Diamant
 *   B: 3650 écoutes totales artiste
 *
 * ✨ MYTHIQUE
 *   A: Être Légende + 2 titres à CHAQUE niveau + 2 albums à CHAQUE niveau
 *   B: 7000 écoutes totales artiste
 *
 * Règles :
 * - Statut PERMANENT (jamais perdu)
 * - Artistes sans statut = invisibles dans le Panthéon
 * - Calcul automatique après chaque écoute valide
 */
enum class PantheonStatus {

    STAR,
    SUPERSTAR,
    MEGASTAR,
    LEGENDE,
    MYTHIQUE;

    // ── Option B : seuil total écoutes ───────────────────────────────────

    fun totalPlaysThreshold(): Int = when (this) {
        STAR      -> 425
        SUPERSTAR -> 650
        MEGASTAR  -> 1250
        LEGENDE   -> 3650
        MYTHIQUE  -> 7000
    }

    // ── Option A : nombre de chansons certifiées requis ──────────────────

    fun requiredCertifiedSongs(): Int = when (this) {
        STAR      -> 5
        SUPERSTAR -> 4
        MEGASTAR  -> 5
        LEGENDE   -> 5
        MYTHIQUE  -> 2  // 2 à CHAQUE niveau
    }

    // ── Option A : niveau minimum de certification chanson requis ─────────

    fun requiredSongLevel(): CertificationLevel = when (this) {
        STAR      -> CertificationLevel.SILVER
        SUPERSTAR -> CertificationLevel.GOLD
        MEGASTAR  -> CertificationLevel.PLATINUM
        LEGENDE   -> CertificationLevel.DIAMOND
        MYTHIQUE  -> CertificationLevel.SILVER  // tous les niveaux requis
    }

    // ── Option A : nombre d'albums certifiés requis ───────────────────────

    fun requiredCertifiedAlbums(): Int = when (this) {
        STAR      -> 2
        SUPERSTAR -> 2
        MEGASTAR  -> 2
        LEGENDE   -> 2
        MYTHIQUE  -> 2  // 2 à CHAQUE niveau
    }

    // ── Option A : niveau minimum de certification album requis ───────────

    fun requiredAlbumLevel(): CertificationLevel = when (this) {
        STAR      -> CertificationLevel.SILVER
        SUPERSTAR -> CertificationLevel.GOLD
        MEGASTAR  -> CertificationLevel.PLATINUM
        LEGENDE   -> CertificationLevel.DIAMOND
        MYTHIQUE  -> CertificationLevel.SILVER  // tous les niveaux requis
    }

    // ── Helpers UI ────────────────────────────────────────────────────────

    fun emoji(): String = when (this) {
        STAR      -> "⭐"
        SUPERSTAR -> "🌟"
        MEGASTAR  -> "👑"
        LEGENDE   -> "🏛️"
        MYTHIQUE  -> "✨"
    }

    fun displayName(): String = when (this) {
        STAR      -> "Star"
        SUPERSTAR -> "Superstar"
        MEGASTAR  -> "Megastar"
        LEGENDE   -> "Légende"
        MYTHIQUE  -> "Mythique"
    }

    /** Priorité pour le tri (plus haut = meilleur) */
    fun rank(): Int = when (this) {
        STAR      -> 1
        SUPERSTAR -> 2
        MEGASTAR  -> 3
        LEGENDE   -> 4
        MYTHIQUE  -> 5
    }
}