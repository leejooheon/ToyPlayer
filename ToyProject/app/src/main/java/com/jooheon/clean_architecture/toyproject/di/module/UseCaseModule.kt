package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.domain.repository.*
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCase
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCase
import com.jooheon.clean_architecture.domain.usecase.map.ParkingSpotUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCaseImpl
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
    fun provideGithubApi(repository: GithubRepository): GithubUseCase =
        GithubUseCaseImpl(repository)

    @Provides
    @Singleton
    fun provideWikipediaApi(repository: WikipediaRepository): WikipediaUseCase =
        WikipediaUseCaseImpl(repository)


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
    fun provideMusicUseCase(repository: MusicRepository): MusicUseCase =
        MusicUseCaseImpl(repository)
}