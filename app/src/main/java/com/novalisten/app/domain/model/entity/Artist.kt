// domain/model/entity/Artist.kt
package com.novalisten.app.domain.model.entity

/**
 * Modèle métier d'un artiste.
 * Pur Kotlin — aucune dépendance Android/Room.
 */
data class Artist(
    val id: Long,
    val name: String,
    val normalizedName: String,
    val genre: String?,
    val country: String?,
    val imageUrl: String?,
    val bioSummary: String?,
    val spotifyId: String?,
    val musicBrainzId: String?,
    val createdAt: Long,
    val updatedAt: Long
)