// domain/model/enums/Period.kt
package com.novalisten.app.domain.model.enums

/**
 * Périodes disponibles dans NovaListen.
 * Utilisées dans Stats, Billboard, Records, Awards.
 * Toutes les périodes sont CALENDAIRES (pas glissantes).
 */
enum class Period {

    /** Journée calendaire (00:00 → 23:59) */
    DAILY,

    /** Semaine calendaire (Lundi → Dimanche) */
    WEEKLY,

    /** Mois calendaire (1er → dernier jour) */
    MONTHLY,

    /** Année calendaire (1er Jan → 31 Déc) */
    YEARLY,

    /** Depuis la toute première écoute */
    GLOBAL;

    /** Libellé affiché dans l'UI */
    fun displayName(): String = when (this) {
        DAILY   -> "Daily"
        WEEKLY  -> "Weekly"
        MONTHLY -> "Monthly"
        YEARLY  -> "Yearly"
        GLOBAL  -> "Global"
    }
}