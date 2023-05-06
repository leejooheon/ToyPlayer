package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.data.datasource.GithubRemoteDataSource
import com.jooheon.clean_architecture.data.datasource.SubwayRemoteDataSource
import com.jooheon.clean_architecture.data.datasource.TempDataSource
import com.jooheon.clean_architecture.data.datasource.WikipediaRemoteDataSource
import com.jooheon.clean_architecture.data.datasource.local.ParkingSpotDataSource
import com.jooheon.clean_architecture.data.local.AppPreferences
import com.jooheon.clean_architecture.data.repository.*
import com.jooheon.clean_architecture.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideGithubRepository(
        githubRemoteDataSource: GithubRemoteDataSource,
        databaseSource: TempDataSource
    ): GithubRepository {
        return GithubRepositoryImpl(githubRemoteDataSource, databaseSource)
    }

    @Provides
    @Singleton
    fun provideWikipediaRepository(
        wikipediaRemoteDataSource: WikipediaRemoteDataSource,
        databaseSource: TempDataSource
    ): WikipediaRepository {
        return WikipediaRepositoryImpl(wikipediaRemoteDataSource, databaseSource)
    }
    @Provides
    @Singleton
    fun provideSubwayRepository(
        subwayRemoteDataSource: SubwayRemoteDataSource,
    ): SubwayRepository {
        return SubwayRepositoryImpl(subwayRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideFirebaseTokenRepository(
        appPreferences: AppPreferences
    ): FirebaseTokenRepository {
        return FirebaseTokenRepositoryImpl(appPreferences)
    }

    @Provides
    @Singleton
    fun provideParkingSpotRepository(
        parkingSpotDataSource: ParkingSpotDataSource
    ): ParkingSpotRepository {
        return ParkingSpotRepositoryImpl(parkingSpotDataSource)
    }

    @Provides
    fun provideSettingRepository(appPreferences: AppPreferences): SettingRepository =
        SettingRepositoryImpl(appPreferences)

}