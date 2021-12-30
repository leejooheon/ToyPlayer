package com.example.rxtest.di.module

import com.example.rxtest.data.api.GithubApi
import com.example.rxtest.data.datasource.GithubApiDataSource
import com.example.rxtest.data.datasource.TempDataSource
import com.example.rxtest.data.repository.GithubRepositoryImpl
import com.example.rxtest.domain.repository.GithubRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideGithubRepository(
        apiSource: GithubApiDataSource,
        databaseSource: TempDataSource
    ): GithubRepository {
        return GithubRepositoryImpl(apiSource, databaseSource)
    }
}