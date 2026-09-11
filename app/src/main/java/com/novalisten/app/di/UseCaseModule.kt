// di/UseCaseModule.kt
package com.novalisten.app.di

import com.novalisten.app.domain.repository.IAchievementRepository
import com.novalisten.app.domain.repository.IBillboardRepository
import com.novalisten.app.domain.repository.ICertificationRepository
import com.novalisten.app.domain.repository.IPantheonRepository
import com.novalisten.app.domain.repository.IPlayRepository
import com.novalisten.app.domain.usecase.awards.ComputeNovaAwardsUseCase
import com.novalisten.app.domain.usecase.awards.IsNovaAwardsUnlockedUseCase
import com.novalisten.app.domain.usecase.billboard.ComputeBillboardSnapshotUseCase
import com.novalisten.app.domain.usecase.billboard.GetBillboardSnapshotUseCase
import com.novalisten.app.domain.usecase.certifications.GetCertificationRadarUseCase
import com.novalisten.app.domain.usecase.certifications.GetCertificationsRankingUseCase
import com.novalisten.app.domain.usecase.certifications.RecomputeCertificationsUseCase
import com.novalisten.app.domain.usecase.halloffame.EvaluateHallOfFameUseCase
import com.novalisten.app.domain.usecase.pantheon.ComputePantheonStatusUseCase
import com.novalisten.app.domain.usecase.pantheon.GetPantheonListUseCase
import com.novalisten.app.domain.usecase.pantheon.GetPantheonSoonUseCase
import com.novalisten.app.domain.usecase.records.GetRecordsUseCase
import com.novalisten.app.domain.usecase.stats.GetGlobalStatsUseCase
import com.novalisten.app.domain.usecase.stats.GetStreakUseCase
import com.novalisten.app.domain.usecase.stats.GetTopAlbumsUseCase
import com.novalisten.app.domain.usecase.stats.GetTopArtistsUseCase
import com.novalisten.app.domain.usecase.stats.GetTopTracksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt — Fournit tous les Use Cases.
 * Singleton car partagés entre ViewModels.
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // ── Stats ──────────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideGetTopTracksUseCase(
        playRepository: IPlayRepository
    ) = GetTopTracksUseCase(playRepository)

    @Provides @Singleton
    fun provideGetTopArtistsUseCase(
        playRepository: IPlayRepository
    ) = GetTopArtistsUseCase(playRepository)

    @Provides @Singleton
    fun provideGetTopAlbumsUseCase(
        playRepository: IPlayRepository
    ) = GetTopAlbumsUseCase(playRepository)

    @Provides @Singleton
    fun provideGetStreakUseCase(
        playRepository: IPlayRepository
    ) = GetStreakUseCase(playRepository)

    @Provides @Singleton
    fun provideGetGlobalStatsUseCase(
        playRepository: IPlayRepository
    ) = GetGlobalStatsUseCase(playRepository)

    // ── Certifications ─────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideRecomputeCertificationsUseCase(
        certificationRepository: ICertificationRepository,
        playRepository: IPlayRepository
    ) = RecomputeCertificationsUseCase(certificationRepository, playRepository)

    @Provides @Singleton
    fun provideGetCertificationsRankingUseCase(
        certificationRepository: ICertificationRepository
    ) = GetCertificationsRankingUseCase(certificationRepository)

    @Provides @Singleton
    fun provideGetCertificationRadarUseCase(
        certificationRepository: ICertificationRepository
    ) = GetCertificationRadarUseCase(certificationRepository)

    // ── Panthéon ───────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideComputePantheonStatusUseCase(
        pantheonRepository: IPantheonRepository,
        certificationRepository: ICertificationRepository,
        playRepository: IPlayRepository
    ) = ComputePantheonStatusUseCase(
        pantheonRepository, certificationRepository, playRepository
    )

    @Provides @Singleton
    fun provideGetPantheonListUseCase(
        pantheonRepository: IPantheonRepository
    ) = GetPantheonListUseCase(pantheonRepository)

    @Provides @Singleton
    fun provideGetPantheonSoonUseCase(
        pantheonRepository: IPantheonRepository
    ) = GetPantheonSoonUseCase(pantheonRepository)

    // ── Billboard ──────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideComputeBillboardSnapshotUseCase(
        playRepository: IPlayRepository,
        billboardRepository: IBillboardRepository
    ) = ComputeBillboardSnapshotUseCase(playRepository, billboardRepository)

    @Provides @Singleton
    fun provideGetBillboardSnapshotUseCase(
        billboardRepository: IBillboardRepository
    ) = GetBillboardSnapshotUseCase(billboardRepository)

    // ── Hall of Fame ───────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideEvaluateHallOfFameUseCase(
        billboardRepository: IBillboardRepository
    ) = EvaluateHallOfFameUseCase(billboardRepository)

    // ── Records ────────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideGetRecordsUseCase(
        achievementRepository: IAchievementRepository
    ) = GetRecordsUseCase(achievementRepository)

    // ── Awards ─────────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideIsNovaAwardsUnlockedUseCase(
        achievementRepository: IAchievementRepository
    ) = IsNovaAwardsUnlockedUseCase(achievementRepository)

    @Provides @Singleton
    fun provideComputeNovaAwardsUseCase(
        playRepository: IPlayRepository,
        achievementRepository: IAchievementRepository,
        isUnlockedUseCase: IsNovaAwardsUnlockedUseCase
    ) = ComputeNovaAwardsUseCase(
        playRepository, achievementRepository, isUnlockedUseCase
    )
}