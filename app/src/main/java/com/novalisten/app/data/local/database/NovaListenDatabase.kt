// data/local/database/NovaListenDatabase.kt
package com.novalisten.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.novalisten.app.data.local.database.dao.AlbumDao
import com.novalisten.app.data.local.database.dao.ApiCacheDao
import com.novalisten.app.data.local.database.dao.ArtistDao
import com.novalisten.app.data.local.database.dao.BillboardDao
import com.novalisten.app.data.local.database.dao.CertificationDao
import com.novalisten.app.data.local.database.dao.NovaAchievementDao
import com.novalisten.app.data.local.database.dao.PantheonDao
import com.novalisten.app.data.local.database.dao.PeriodStatsDao
import com.novalisten.app.data.local.database.dao.PlayDao
import com.novalisten.app.data.local.database.dao.TrackDao
import com.novalisten.app.data.local.database.entity.AlbumEntity
import com.novalisten.app.data.local.database.entity.ApiCacheEntity
import com.novalisten.app.data.local.database.entity.ArtistEntity
import com.novalisten.app.data.local.database.entity.BillboardEntity
import com.novalisten.app.data.local.database.entity.CertificationEntity
import com.novalisten.app.data.local.database.entity.NovaAchievementEntity
import com.novalisten.app.data.local.database.entity.PantheonEntity
import com.novalisten.app.data.local.database.entity.PeriodStatsEntity
import com.novalisten.app.data.local.database.entity.PlayEntity
import com.novalisten.app.data.local.database.entity.TrackEntity

/**
 * Base de données principale de NovaListen.
 *
 * 10 tables :
 * 1. tracks           → Morceaux détectés
 * 2. artists          → Artistes
 * 3. albums           → Albums
 * 4. plays            → Écoutes (cœur de NovaListen)
 * 5. period_stats     → Agrégats pré-calculés (performance)
 * 6. certifications   → Certifications chansons/albums
 * 7. pantheon         → Statuts artistes
 * 8. billboard        → Classements + Hall of Fame intégré
 * 9. nova_achievements → Awards + Records
 * 10. api_cache       → Cache APIs + sync log
 */
@Database(
    entities = [
        TrackEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        PlayEntity::class,
        PeriodStatsEntity::class,
        CertificationEntity::class,
        PantheonEntity::class,
        BillboardEntity::class,
        NovaAchievementEntity::class,
        ApiCacheEntity::class
    ],
    version      = 1,
    exportSchema = false
)
abstract class NovaListenDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun playDao(): PlayDao
    abstract fun periodStatsDao(): PeriodStatsDao
    abstract fun certificationDao(): CertificationDao
    abstract fun pantheonDao(): PantheonDao
    abstract fun billboardDao(): BillboardDao
    abstract fun novaAchievementDao(): NovaAchievementDao
    abstract fun apiCacheDao(): ApiCacheDao

    companion object {
        const val DATABASE_NAME = "novalisten.db"
    }
}