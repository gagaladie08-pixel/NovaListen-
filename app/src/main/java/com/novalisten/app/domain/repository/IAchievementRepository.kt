// domain/repository/IAchievementRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.NovaAchievement
import com.novalisten.app.domain.model.enums.AchievementType
import com.novalisten.app.domain.model.enums.NovaAwardCategory
import com.novalisten.app.domain.model.enums.Period
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Achievement (Awards + Records).
 */
interface IAchievementRepository {

    /** Sauvegarde un achievement */
    suspend fun saveAchievement(achievement: NovaAchievement): Long

    /** Met à jour un achievement existant (ex: award LIVE → FINAL) */
    suspend fun updateAchievement(achievement: NovaAchievement)

    /** Récupère un award par catégorie et année */
    suspend fun getAward(
        category: NovaAwardCategory,
        year: Int
    ): NovaAchievement?

    /** Récupère un record par catégorie et période */
    suspend fun getRecord(
        category: String,
        period: Period?
    ): NovaAchievement?

    /** Tous les awards d'une année */
    fun observeAwards(year: Int): Flow<List<NovaAchievement>>

    /** Tous les records */
    fun observeRecords(): Flow<List<NovaAchievement>>

    /** Vérifie si NovaListen est utilisé depuis ≥2 mois (déblocage Awards) */
    suspend fun isNovaAwardsUnlocked(): Boolean
}