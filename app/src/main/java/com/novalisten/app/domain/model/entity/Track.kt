// domain/model/entity/Track.kt
package com.novalisten.app.domain.model.entity

/**
 * Modèle métier d'un morceau.
 * Pur Kotlin — aucune dépendance Android/Room.
 */
data class Track(
    val id: Long,
    val title: String,
    val normalizedTitle: String,
    val artistId: Long,
    val albumId: Long?,
    val durationMs: Long,
    val genre: String?,
    val releaseDate: String?,
    val isExplicit: Boolean,
    val spotifyId: String?,
    val musicBrainzId: String?,
    val isrc: String?,
    val createdAt: Long,
    val updatedAt: Long
)