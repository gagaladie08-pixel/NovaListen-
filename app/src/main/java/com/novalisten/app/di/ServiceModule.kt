// di/ServiceModule.kt
package com.novalisten.app.di

import com.novalisten.app.service.detection.MediaSessionObserver
import com.novalisten.app.service.detection.NotificationObserver
import com.novalisten.app.service.detection.PlaySaver
import com.novalisten.app.service.detection.SessionManager
import com.novalisten.app.service.detection.TrackDeduplicator
import com.novalisten.app.service.detection.TrackNormalizer
import com.novalisten.app.service.detection.TrackValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt — Fournit les composants du service de détection.
 * Tous en Singleton car partagés entre le service et les observers.
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides @Singleton
    fun provideTrackNormalizer(): TrackNormalizer = TrackNormalizer()

    @Provides @Singleton
    fun provideTrackDeduplicator(): TrackDeduplicator = TrackDeduplicator()

    @Provides @Singleton
    fun provideTrackValidator(): TrackValidator = TrackValidator()

    @Provides @Singleton
    fun provideSessionManager(): SessionManager = SessionManager()

    @Provides @Singleton
    fun provideNotificationObserver(): NotificationObserver = NotificationObserver()
}