// service/detection/TrackValidator.kt
package com.novalisten.app.service.detection

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Résultat de la validation d'une écoute.
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val reason: String) : ValidationResult()
}

/**
 * Applique les 6 règles de validation métier.
 *
 * RÈGLE 1 — Durée minimale : >= 30 000ms
 * RÈGLE 2 — Taux de complétion : calculé (pas bloquant)
 * RÈGLE 3 — Pause longue : > 20 min → session terminée
 * RÈGLE 4 — Anti-doublon : géré par TrackDeduplicator
 * RÈGLE 5 — Source de confiance : MediaSession > Notification
 * RÈGLE 6 — État de l'app : PLAYING requis
 */
@Singleton
class TrackValidator @Inject constructor() {

    companion object {
        /** Durée minimale d'écoute valide : 30 secondes */
        const val MIN_LISTEN_DURATION_MS = 30_000L

        /** Pause max avant fin de session : 20 minutes */
        const val MAX_PAUSE_DURATION_MS = 20 * 60 * 1_000L
    }

    /**
     * Valide une écoute selon les règles métier.
     *
     * @param durationListenedMs Durée réellement écoutée en ms
     * @param event              Événement brut normalisé
     */
    fun validate(
        durationListenedMs: Long,
        event: RawTrackEvent
    ): ValidationResult {

        // RÈGLE 1 — Durée minimale
        if (durationListenedMs < MIN_LISTEN_DURATION_MS) {
            return ValidationResult.Invalid(
                "Durée insuffisante : ${durationListenedMs}ms < ${MIN_LISTEN_DURATION_MS}ms"
            )
        }

        // RÈGLE 6 — État requis : PLAYING
        if (event.state == PlaybackState.STOPPED) {
            return ValidationResult.Invalid("État STOPPED — pas une écoute valide")
        }

        // RÈGLE 1 bis — Titre et artiste non vides
        if (event.title.isBlank() || event.artist.isBlank()) {
            return ValidationResult.Invalid("Titre ou artiste manquant")
        }

        return ValidationResult.Valid
    }

    /**
     * Calcule le taux de complétion.
     * Non bloquant — stocké pour stats avancées.
     */
    fun completionRate(
        durationListenedMs: Long,
        totalDurationMs: Long
    ): Float {
        if (totalDurationMs <= 0) return 0f
        return (durationListenedMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)
    }

    /**
     * Vérifie si une pause dépasse le seuil de 20 min.
     */
    fun isPauseTooLong(pauseDurationMs: Long): Boolean =
        pauseDurationMs > MAX_PAUSE_DURATION_MS
}