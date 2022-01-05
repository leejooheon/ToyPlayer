package com.example.rxtest.di.module

import com.jooheon.clean_architecture.repository.GithubRepository
import com.jooheon.clean_architecture.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.usecase.github.GithubUseCaseImpl
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