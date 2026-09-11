// domain/model/enums/CertificationLevel.kt
package com.novalisten.app.domain.model.enums

/**
 * Niveaux de certification NovaListen.
 *
 * CHANSONS :
 *   Argent   →    25 écoutes
 *   Or       →    50 écoutes
 *   Platine  →   100 écoutes
 *   Diamant  →   350 écoutes (puis +350 par multiplicateur)
 *
 * ALBUMS :
 *   Argent   →    50 écoutes
 *   Or       →   100 écoutes
 *   Platine  →   200 écoutes
 *   Diamant  →   700 écoutes (puis +700 par multiplicateur)
 *
 * Note : les certifications Diamant sont infinies (x2, x3, x4…)
 * grâce au diamondMultiplier dans CertificationState.
 */
enum class CertificationLevel {

    SILVER,    // Argent
    GOLD,      // Or
    PLATINUM,  // Platine
    DIAMOND;   // Diamant (+ multi-diamant via multiplicateur)

    // ── Seuils Chansons ───────────────────────────────────────────────────

    fun songThreshold(): Int = when (this) {
        SILVER   -> 25
        GOLD     -> 50
        PLATINUM -> 100
        DIAMOND  -> 350
    }

    /** Seuil multi-diamant chanson : 350 × multiplicateur */
    fun songDiamondThreshold(multiplier: Int): Int = 350 * multiplier

    // ── Seuils Albums ─────────────────────────────────────────────────────

    fun albumThreshold(): Int = when (this) {
        SILVER   -> 50
        GOLD     -> 100
        PLATINUM -> 200
        DIAMOND  -> 700
    }

    /** Seuil multi-diamant album : 700 × multiplicateur */
    fun albumDiamondThreshold(multiplier: Int): Int = 700 * multiplier

    // ── Helpers UI ────────────────────────────────────────────────────────

    fun emoji(): String = when (this) {
        SILVER   -> "🥉"
        GOLD     -> "🥈"
        PLATINUM -> "🥇"
        DIAMOND  -> "💎"
    }

    fun displayName(): String = when (this) {
        SILVER   -> "Argent"
        GOLD     -> "Or"
        PLATINUM -> "Platine"
        DIAMOND  -> "Diamant"
    }

    /** Priorité (plus haut = meilleur) */
    fun rank(): Int = when (this) {
        SILVER   -> 1
        GOLD     -> 2
        PLATINUM -> 3
        DIAMOND  -> 4
    }
}