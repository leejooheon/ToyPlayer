package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.domain.repository.*
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCase
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCase
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.subway.SubwayUseCase
import com.jooheon.clean_architecture.domain.usecase.subway.SubwayUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCase
import com.jooheon.clean_architecture.domain.usecase.wikipedia.WikipediaUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideGithubUseCase(repository: GithubRepository): GithubUseCase =
        GithubUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideWikipediaUseCase(repository: WikipediaRepository): WikipediaUseCase =
        WikipediaUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideSubwayUseCase(repository: SubwayRepository): SubwayUseCase =
        SubwayUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideFirebaseTokenUseCase(firebaseTokenRepository: FirebaseTokenRepository): FirebaseTokenUseCase =
        FirebaseTokenUseCaseImpl(firebaseTokenRepository)

    @Provides
    @Singleton
    fun provideParkingSpotUseCase(repository: ParkingSpotRepository): ParkingSpotUseCase =
        ParkingSpotUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideSettingUseCase(settingRepository: SettingRepository): SettingUseCase =
        SettingUseCaseImpl(settingRepository)
}