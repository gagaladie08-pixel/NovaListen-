// domain/model/enums/ChartType.kt
package com.novalisten.app.domain.model.enums

/**
 * Les 3 classements Billboard de NovaListen.
 * Chaque chart a sa propre limite par période.
 */
enum class ChartType {

    /** Top 100 chansons (Daily: Top 75) */
    NOVA_HOT_100,

    /** Top 50 artistes (Daily: Top 25) */
    NOVA_ARTIST_50,

    /** Top 75 albums (Daily: Top 50) */
    NOVA_75_ALBUMS;

    /**
     * Limite d'entrées selon la période.
     * Daily a des limites réduites.
     */
    fun limitForPeriod(period: Period): Int = when (this) {
        NOVA_HOT_100    -> if (period == Period.DAILY) 75  else 100
        NOVA_ARTIST_50  -> if (period == Period.DAILY) 25  else 50
        NOVA_75_ALBUMS  -> if (period == Period.DAILY) 50  else 75
    }

    fun displayName(): String = when (this) {
        NOVA_HOT_100   -> "Nova Hot 100"
        NOVA_ARTIST_50 -> "Nova Artist 50"
        NOVA_75_ALBUMS -> "Nova 75 Albums"
    }
}