// domain/repository/IPlayRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.Play
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Period
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Play — Le plus critique de NovaListen.
 *
 * Règle de tri (appliquée dans TOUTES les requêtes) :
 * → Tri principal  : playCount DESC
 * → Tri secondaire : listenTimeMs DESC (départage égalités)
 */
interface IPlayRepository {

    /** Sauvegarde une écoute validée */
    suspend fun savePlay(play: Play): Long

    /** Compte les écoutes valides d'une entité sur une période */
    suspend fun getPlayCount(
        entityId: Long,
        entityType: EntityType,
        period: Period,
        periodKey: String
    ): Int

    /** Temps d'écoute total d'une entité sur une période */
    suspend fun getListenTime(
        entityId: Long,
        entityType: EntityType,
        period: Period,
        periodKey: String
    ): Long

    /** Top N entités sur une période (tri: playCount DESC, listenTimeMs DESC) */
    suspend fun getTopEntities(
        entityType: EntityType,
        period: Period,
        periodKey: String,
        limit: Int
    ): List<Pair<Long, Int>>  // entityId → playCount

    /** Flux d'écoutes récentes (pour UI live) */
    fun observeRecentPlays(limit: Int = 20): Flow<List<Play>>

    /** Écoutes d'une session spécifique */
    suspend fun getPlaysBySession(sessionId: String): List<Play>

    /** Nombre total d'écoutes valides de l'artiste (pour Panthéon Option B) */
    suspend fun getTotalValidPlaysForArtist(artistId: Long): Int

    /** Streak actuel (jours consécutifs avec ≥1 écoute valide) */
    suspend fun getCurrentStreak(): Int

    /** Plus long streak historique */
    suspend fun getLongestStreak(): Int
}