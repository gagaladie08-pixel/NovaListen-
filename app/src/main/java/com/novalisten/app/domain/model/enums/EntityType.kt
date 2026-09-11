// domain/model/enums/EntityType.kt
package com.novalisten.app.domain.model.enums

/**
 * Les 3 types d'entités analysées dans NovaListen.
 * Songs / Artists / Albums — utilisés partout
 * (Stats, Billboard, Certifications, Panthéon, Records, Awards).
 */
enum class EntityType {

    SONG,
    ARTIST,
    ALBUM;

    fun displayName(): String = when (this) {
        SONG   -> "Songs"
        ARTIST -> "Artists"
        ALBUM  -> "Albums"
    }
}