// data/repository/PlayRepositoryImpl.kt
package com.novalisten.app.data.repository

import com.novalisten.app.data.local.database.dao.PlayDao
import com.novalisten.app.data.local.database.entity.PlayEntity
import com.novalisten.app.domain.model.entity.Play
import com.novalisten.app.domain.model.enums.EntityType
import com.novalisten.app.domain.model.enums.Period
import com.novalisten.app.domain.repository.IPlayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class PlayRepositoryImpl @Inject constructor(
    private val playDao: PlayDao
) : IPlayRepository {

    override suspend fun savePlay(play: Play): Long =
        playDao.insert(PlayEntity.fromDomain(play))

    override suspend fun getPlayCount(
        entityId: Long,
        entityType: EntityType,
        period: Period,
        periodKey: String
    ): Int {
        val (startMs, endMs) = periodToRange(period, periodKey)
        return when (entityType) {
            EntityType.ARTIST -> playDao.countValidPlaysForArtist(entityId, startMs, endMs)
            EntityType.SONG   -> playDao.countValidPlaysForTrack(entityId, startMs, endMs)
            EntityType.ALBUM  -> 0 // Album = somme des tracks (calculé via CertificationRepo)
        }
    }

    override suspend fun getListenTime(
        entityId: Long,
        entityType: EntityType,
        period: Period,
        periodKey: String
    ): Long {
        val (startMs, endMs) = periodToRange(period, periodKey)
        return when (entityType) {
            EntityType.ARTIST -> playDao.sumListenTimeForArtist(entityId, startMs, endMs)
            else              -> 0L
        }
    }

    override suspend fun getTopEntities(
        entityType: EntityType,
        period: Period,
        periodKey: String,
        limit: Int
    ): List<Pair<Long, Int>> {
        val (startMs, endMs) = periodToRange(period, periodKey)
        return when (entityType) {
            EntityType.ARTIST -> playDao.topArtists(startMs, endMs, limit)
                .map { it.entity_id to it.play_count }
            EntityType.SONG   -> playDao.topTracks(startMs, endMs, limit)
                .map { it.entity_id to it.play_count }
            EntityType.ALBUM  -> playDao.topAlbums(startMs, endMs, limit)
                .map { it.entity_id to it.play_count }
        }
    }

    override fun observeRecentPlays(limit: Int): Flow<List<Play>> =
        playDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun getPlaysBySession(sessionId: String): List<Play> =
        playDao.getBySession(sessionId).map { it.toDomain() }

    override suspend fun getTotalValidPlaysForArtist(artistId: Long): Int =
        playDao.totalValidPlaysForArtist(artistId)

    override suspend fun getCurrentStreak(): Int {
        val days = playDao.getDistinctDaysWithPlays()
        if (days.isEmpty()) return 0
        var streak = 1
        val today = LocalDate.now(ZoneId.systemDefault()).toString()
        if (days.first() != today) return 0
        for (i in 0 until days.size - 1) {
            val current = LocalDate.parse(days[i])
            val next    = LocalDate.parse(days[i + 1])
            if (current.minusDays(1) == next) streak++ else break
        }
        return streak
    }

    override suspend fun getLongestStreak(): Int {
        val days = playDao.getDistinctDaysWithPlays().reversed()
        if (days.isEmpty()) return 0
        var longest = 1
        var current = 1
        for (i in 0 until days.size - 1) {
            val d1 = LocalDate.parse(days[i])
            val d2 = LocalDate.parse(days[i + 1])
            if (d2.minusDays(1) == d1) {
                current++
                if (current > longest) longest = current
            } else {
                current = 1
            }
        }
        return longest
    }

    // ── Helper : convertit une période en range timestamp ─────────────────

    private fun periodToRange(period: Period, periodKey: String): Pair<Long, Long> {
        val zone = ZoneId.systemDefault()
        return when (period) {
            Period.DAILY -> {
                val date = LocalDate.parse(periodKey)
                val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
                val end   = date.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
                start to end
            }
            Period.WEEKLY -> {
                // periodKey format : "2026-W37"
                val parts = periodKey.split("-W")
                val year  = parts[0].toInt()
                val week  = parts[1].toInt()
                val date  = LocalDate.ofYearDay(year, 1)
                    .with(java.time.temporal.WeekFields.ISO.weekOfYear(), week.toLong())
                    .with(java.time.DayOfWeek.MONDAY)
                val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
                val end   = date.plusWeeks(1).atStartOfDay(zone).toInstant().toEpochMilli()
                start to end
            }
            Period.MONTHLY -> {
                val parts = periodKey.split("-")
                val date  = LocalDate.of(parts[0].toInt(), parts[1].toInt(), 1)
                val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
                val end   = date.plusMonths(1).atStartOfDay(zone).toInstant().toEpochMilli()
                start to end
            }
            Period.YEARLY -> {
                val year  = periodKey.toInt()
                val date  = LocalDate.of(year, 1, 1)
                val start = date.atStartOfDay(zone).toInstant().toEpochMilli()
                val end   = date.plusYears(1).atStartOfDay(zone).toInstant().toEpochMilli()
                start to end
            }
            Period.GLOBAL -> 0L to Long.MAX_VALUE
        }
    }
}