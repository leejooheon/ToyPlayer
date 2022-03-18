package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.data.api.GithubApi
import com.jooheon.clean_architecture.data.api.WikipediaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkServiceModule {

    @Provides
    @Singleton
    fun providesGithubApi(@Named(Constants.GITHUB_RETROFIT) retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

    @Provides
    @Singleton
    fun providesWikipediaApi(@Named(Constants.WIKI_RETROFIT) retrofit: Retrofit): WikipediaApi = retrofit.create(WikipediaApi::class.java)

}