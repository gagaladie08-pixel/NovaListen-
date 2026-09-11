// domain/model/enums/Movement.kt
package com.novalisten.app.domain.model.enums

/**
 * Mouvements Billboard entre deux périodes.
 * Comparaison :
 *   Daily   → vs hier
 *   Weekly  → vs semaine précédente
 *   Monthly → vs mois précédent
 *   Yearly  → vs année précédente
 *   Global  → vs semaine précédente (all-time)
 */
enum class Movement {

    /** Montée dans le classement (value = nb de places) */
    UP,

    /** Descente dans le classement (value = nb de places) */
    DOWN,

    /** Même position */
    STABLE,

    /** Nouvelle entrée dans le chart */
    NEW,

    /** Retour dans le chart après absence */
    REENTRY;

    /**
     * Affichage avec la valeur du mouvement.
     * Ex: UP(5) → "↑ +5" | DOWN(3) → "↓ -3"
     */
    fun display(value: Int = 0): String = when (this) {
        UP      -> "↑ +$value"
        DOWN    -> "↓ -$value"
        STABLE  -> "="
        NEW     -> "🆕 NEW"
        REENTRY -> "↩️ RE"
    }
}
