package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.data.datasource.GithubRemoteDataSource
import com.jooheon.clean_architecture.data.datasource.TempDataSource
import com.jooheon.clean_architecture.data.datasource.WikipediaRemoteDataSource
import com.jooheon.clean_architecture.data.local.AppPreferences
import com.jooheon.clean_architecture.data.repository.FirebaseTokenRepositoryImpl
import com.jooheon.clean_architecture.data.repository.GithubRepositoryImpl
import com.jooheon.clean_architecture.data.repository.WikipediaRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.FirebaseTokenRepository
import com.jooheon.clean_architecture.domain.repository.GithubRepository
import com.jooheon.clean_architecture.domain.repository.WikipediaRepository
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
    fun provideFirebaseTokenRepository(
        appPreferences: AppPreferences
    ): FirebaseTokenRepository {
        return FirebaseTokenRepositoryImpl(appPreferences)
    }

}