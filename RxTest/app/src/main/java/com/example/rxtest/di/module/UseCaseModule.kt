package com.example.rxtest.di.module

import com.example.rxtest.domain.repository.GithubRepository
import com.example.rxtest.domain.usecase.github.GithubUseCase
import com.example.rxtest.domain.usecase.github.GithubUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.hosseinabbasi.presentation.common.transformer.AsyncSTransformer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Provides
    @Singleton
    fun provideGithubApi(repository: GithubRepository): GithubUseCase =
        GithubUseCaseImpl(
            AsyncSTransformer(),
            AsyncSTransformer(),
            repository
        )
}