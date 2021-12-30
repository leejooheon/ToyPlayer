package com.example.rxtest.di.module.activity

import com.example.rxtest.data.api.GithubApi
import com.example.rxtest.data.datasource.GithubApiDataSource
import com.example.rxtest.data.repository.GithubRepositoryImpl
import com.example.rxtest.domain.repository.GithubRepository
import com.example.rxtest.domain.usecase.github.GithubUseCase
import com.example.rxtest.domain.usecase.github.GithubUseCaseImpl
import dagger.Module
import dagger.Provides
import ir.hosseinabbasi.presentation.common.transformer.AsyncSTransformer
import java.util.*

@Module
class MainActivityModule {

    @Provides
    fun provideDatabaseSource(): Objects?{
        return null
    }

    @Provides
    fun provideApiSource(api: GithubApi): GithubApiDataSource = GithubApiDataSource(api)

    @Provides
    fun provideRepository(
        apiSource: GithubApiDataSource,
        databaseSource: Objects? // 현재 미구현 상태
    ): GithubRepository {
        return GithubRepositoryImpl(apiSource, databaseSource)
    }

    @Provides
    fun provideGithubApi(repository: GithubRepository): GithubUseCase =
        GithubUseCaseImpl(
            AsyncSTransformer(),
            AsyncSTransformer(),
            repository
        )
}