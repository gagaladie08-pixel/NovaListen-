// service/detection/TrackDeduplicator.kt
package com.novalisten.app.service.detection

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Anti-doublon pour la détection musicale.
 *
 * Règles :
 * - Même morceau dans les 5 secondes → IGNORÉ (rebond)
 * - Même morceau après fin → NOUVELLE écoute
 * - État PAUSED → on conserve l'écoute en cours
 * - État STOPPED → on termine l'écoute
 */
@Singleton
class TrackDeduplicator @Inject constructor() {

    companion object {
        /** Fenêtre de déduplication en ms (5 secondes) */
        const val DEBOUNCE_WINDOW_MS = 5_000L
    }

    /** Dernière clé unique de morceau détecté */
    private var lastKey: String? = null

    /** Timestamp de la dernière détection */
    private var lastDetectedAt: Long = 0L

    /**
     * Vérifie si l'événement est un doublon.
     *
     * @return true si doublon (à ignorer), false si nouveau
     */
    fun isDuplicate(event: RawTrackEvent): Boolean {
        val key  = event.uniqueKey()
        val now  = System.currentTimeMillis()
        val diff = now - lastDetectedAt

        // Même morceau dans la fenêtre de 5s → doublon
        if (key == lastKey && diff < DEBOUNCE_WINDOW_MS) {
            return true
        }

        // Nouveau morceau ou hors fenêtre → pas un doublon
        lastKey         = key
        lastDetectedAt  = now
        return false
    }

    /**
     * Réinitialise l'état (ex: après une pause longue).
     */
    fun reset() {
        lastKey        = null
        lastDetectedAt = 0L
    }
}