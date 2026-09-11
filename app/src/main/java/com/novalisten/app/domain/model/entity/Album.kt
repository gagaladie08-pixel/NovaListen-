// domain/model/entity/Album.kt
package com.novalisten.app.domain.model.entity

/**
 * Type d'album NovaListen.
 */
enum class AlbumType {
    ALBUM, EP, SINGLE, COMPILATION
}

/**
 * Modèle métier d'un album.
 * Pur Kotlin — aucune dépendance Android/Room.
 */
data class Album(
    val id: Long,
    val title: String,
    val normalizedTitle: String,
    val artistId: Long,
    val albumType: AlbumType,
    val releaseDate: String?,
    val totalTracks: Int?,
    val coverUrl: String?,
    val spotifyId: String?,
    val musicBrainzId: String?,
    val createdAt: Long,
    val updatedAt: Long
)