package com.example.rxtest.di.module

import com.jooheon.clean_architecture.domain.repository.GithubRepository
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCaseImpl
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
}