package com.example.rxtest.di.module

import com.example.rxtest.data.datasource.GithubRemoteDataSource
import com.example.rxtest.data.datasource.TempDataSource
import com.example.rxtest.data.repository.GithubRepositoryImpl
import com.example.rxtest.domain.repository.GithubRepository
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