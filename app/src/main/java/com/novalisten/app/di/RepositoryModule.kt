// di/RepositoryModule.kt
package com.novalisten.app.di

import com.novalisten.app.data.repository.AchievementRepositoryImpl
import com.novalisten.app.data.repository.AlbumRepositoryImpl
import com.novalisten.app.data.repository.ArtistRepositoryImpl
import com.novalisten.app.data.repository.BillboardRepositoryImpl
import com.novalisten.app.data.repository.CertificationRepositoryImpl
import com.novalisten.app.data.repository.PantheonRepositoryImpl
import com.novalisten.app.data.repository.PlayRepositoryImpl
import com.novalisten.app.data.repository.TrackRepositoryImpl
import com.novalisten.app.domain.repository.IAchievementRepository
import com.novalisten.app.domain.repository.IAlbumRepository
import com.novalisten.app.domain.repository.IArtistRepository
import com.novalisten.app.domain.repository.IBillboardRepository
import com.novalisten.app.domain.repository.ICertificationRepository
import com.novalisten.app.domain.repository.IPantheonRepository
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.repository.ITrackRepository
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt — Lie les interfaces Domain
 * à leurs implémentations Data.
 *
 * Principe Clean Architecture :
 * Le Domain définit les contrats (interfaces).
 * Le Data les implémente.
 * Hilt injecte la bonne implémentation partout.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindTrackRepository(impl: TrackRepositoryImpl): ITrackRepository

    @Binds @Singleton
    abstract fun bindArtistRepository(impl: ArtistRepositoryImpl): IArtistRepository

    @Binds @Singleton
    abstract fun bindAlbumRepository(impl: AlbumRepositoryImpl): IAlbumRepository

    @Binds @Singleton
    abstract fun bindPlayRepository(impl: PlayRepositoryImpl): IPlayRepository

    @Binds @Singleton
    abstract fun bindCertificationRepository(impl: CertificationRepositoryImpl): ICertificationRepository

    @Binds @Singleton
    abstract fun bindPantheonRepository(impl: PantheonRepositoryImpl): IPantheonRepository

    @Binds @Singleton
    abstract fun bindBillboardRepository(impl: BillboardRepositoryImpl): IBillboardRepository

    @Binds @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): IAchievementRepository

    companion object {
        @Provides @Singleton
        fun provideGson(): Gson = Gson()
    }
}