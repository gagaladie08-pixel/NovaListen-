// di/DatabaseModule.kt
package com.novalisten.app.di

import android.content.Context
import androidx.room.Room
import com.novalisten.app.data.local.database.NovaListenDatabase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt — Fournit la BDD et tous les DAOs.
 * Singleton : une seule instance de la BDD dans toute l'app.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): NovaListenDatabase = Room.databaseBuilder(
        context,
        NovaListenDatabase::class.java,
        NovaListenDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration() // En dev seulement — à remplacer par migrations en prod
        .build()

    @Provides fun provideTrackDao(db: NovaListenDatabase): TrackDao = db.trackDao()
    @Provides fun provideArtistDao(db: NovaListenDatabase): ArtistDao = db.artistDao()
    @Provides fun provideAlbumDao(db: NovaListenDatabase): AlbumDao = db.albumDao()
    @Provides fun providePlayDao(db: NovaListenDatabase): PlayDao = db.playDao()
    @Provides fun providePeriodStatsDao(db: NovaListenDatabase): PeriodStatsDao = db.periodStatsDao()
    @Provides fun provideCertificationDao(db: NovaListenDatabase): CertificationDao = db.certificationDao()
    @Provides fun providePantheonDao(db: NovaListenDatabase): PantheonDao = db.pantheonDao()
    @Provides fun provideBillboardDao(db: NovaListenDatabase): BillboardDao = db.billboardDao()
    @Provides fun provideAchievementDao(db: NovaListenDatabase): NovaAchievementDao = db.novaAchievementDao()
    @Provides fun provideApiCacheDao(db: NovaListenDatabase): ApiCacheDao = db.apiCacheDao()
}