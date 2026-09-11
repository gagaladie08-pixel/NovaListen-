// domain/repository/IBillboardRepository.kt
package com.novalisten.app.domain.repository

import com.novalisten.app.domain.model.entity.BillboardEntry
import com.novalisten.app.domain.model.entity.BillboardSnapshot
import com.novalisten.app.domain.model.enums.ChartType
import com.novalisten.app.domain.model.enums.Period
import kotlinx.coroutines.flow.Flow

/**
 * Contrat du repository Billboard.
 * Gère les snapshots et l'historique des classements.
 */
interface IBillboardRepository {

    /** Sauvegarde un snapshot Billboard complet */
    suspend fun saveSnapshot(snapshot: BillboardSnapshot)

    /** Récupère le snapshot d'une période/chart donnée */
    suspend fun getSnapshot(
        chartType: ChartType,
        period: Period,
        periodKey: String
    ): BillboardSnapshot?

    /** Flux du snapshot le plus récent (UI live) */
    fun observeLatestSnapshot(
        chartType: ChartType,
        period: Period
    ): Flow<BillboardSnapshot?>

    /** Historique d'une entité dans un chart */
    suspend fun getEntityHistory(
        entityId: Long,
        chartType: ChartType
    ): List<BillboardEntry>

    /** Peak rank historique d'une entité dans un chart */
    suspend fun getPeakRank(
        entityId: Long,
        chartType: ChartType
    ): Int?

    /** Semaines totales dans un chart */
    suspend fun getWeeksOnChart(
        entityId: Long,
        chartType: ChartType
    ): Int
}