package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.data.api.GithubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    @Provides
    @Singleton
    fun providesGithubApi(retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

}