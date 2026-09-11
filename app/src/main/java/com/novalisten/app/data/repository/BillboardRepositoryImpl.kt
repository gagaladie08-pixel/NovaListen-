// data/repository/BillboardRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.BillboardDao
import com.novalisten.app.data.local.database.entity.BillboardEntity
import com.novalisten.app.domain.model.entity.BillboardEntry
import com.novalisten.app.domain.model.entity.BillboardSnapshot
import com.novalisten.app.domain.model.enums.ChartType
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Movement
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IBillboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BillboardRepositoryImpl @Inject constructor(
    private val billboardDao: BillboardDao
) : IBillboardRepository {

    override suspend fun saveSnapshot(snapshot: BillboardSnapshot) {
        val entities = snapshot.entries.map { it.toEntity(snapshot) }
        billboardDao.upsertAll(entities)
    }

    override suspend fun getSnapshot(
        chartType: ChartType,
        period: Period,
        periodKey: String
    ): BillboardSnapshot? {
        val entries = billboardDao.getSnapshot(
            chartType.name, period.name, periodKey
        )
        if (entries.isEmpty()) return null
        return BillboardSnapshot(
            chartType     = chartType,
            period        = period,
            periodKey     = periodKey,
            calculatedAt  = entries.first().calculatedAt,
            entries       = entries.map { it.toDomain() }
        )
    }

    override fun observeLatestSnapshot(
        chartType: ChartType,
        period: Period
    ): Flow<BillboardSnapshot?> =
        billboardDao.observeLatest(chartType.name, period.name).map { entries ->
            if (entries.isEmpty()) null
            else BillboardSnapshot(
                chartType    = chartType,
                period       = period,
                periodKey    = entries.first().periodKey,
                calculatedAt = entries.first().calculatedAt,
                entries      = entries.map { it.toDomain() }
            )
        }

    override suspend fun getEntityHistory(
        entityId: Long,
        chartType: ChartType
    ): List<BillboardEntry> =
        billboardDao.getEntityHistory(entityId, chartType.name)
            .map { it.toDomain() }

    override suspend fun getPeakRank(
        entityId: Long,
        chartType: ChartType
    ): Int? = billboardDao.getPeakRank(entityId, chartType.name)

    override suspend fun getWeeksOnChart(
        entityId: Long,
        chartType: ChartType
    ): Int = billboardDao.getWeeksOnChart(entityId, chartType.name)

    // ── Mappers ────────────────────────────────────────────────────────────

    private fun BillboardEntity.toDomain(): BillboardEntry = BillboardEntry(
        id             = id,
        entityType     = EntityType.valueOf(entityType),
        entityId       = entityId,
        rank           = rank,
        previousRank   = previousRank,
        peakRank       = peakRank,
        weeksOnChart   = weeksOnChart,
        movement       = Movement.valueOf(movement),
        movementValue  = movementValue,
        playCount      = playCount,
        listenTimeMs   = listenTimeMs,
        hofDirectDebut  = hofDirectDebut,
        hofLongRun      = hofLongRun,
        hofTripleDebut  = hofTripleDebut,
        hofLegendaryRun = hofLegendaryRun,
        hofEntryDate    = hofEntryDate
    )

    private fun BillboardEntry.toEntity(
        snapshot: BillboardSnapshot
    ): BillboardEntity = BillboardEntity(
        id              = id,
        chartType       = snapshot.chartType.name,
        periodType      = snapshot.period.name,
        periodKey       = snapshot.periodKey,
        entityType      = entityType.name,
        entityId        = entityId,
        rank            = rank,
        previousRank    = previousRank,
        peakRank        = peakRank,
        weeksOnChart    = weeksOnChart,
        movement        = movement.name,
        movementValue   = movementValue,
        playCount       = playCount,
        listenTimeMs    = listenTimeMs,
        calculatedAt    = snapshot.calculatedAt,
        hofDirectDebut  = hofDirectDebut,
        hofLongRun      = hofLongRun,
        hofTripleDebut  = hofTripleDebut,
        hofLegendaryRun = hofLegendaryRun,
        hofEntryDate    = hofEntryDate
    )
}