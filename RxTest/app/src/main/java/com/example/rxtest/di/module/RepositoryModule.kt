package com.example.rxtest.di.module

import com.jooheon.clean_architecture.data.datasource.GithubRemoteDataSource
import com.jooheon.clean_architecture.data.datasource.TempDataSource
import com.jooheon.clean_architecture.data.repository.GithubRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.GithubRepository
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
}