package com.jooheon.toyplayer.di.module

import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.data.repository.*
import com.jooheon.toyplayer.domain.repository.*
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
    fun provideFirebaseTokenRepository(
        appPreferences: AppPreferences
    ): FirebaseTokenRepository {
        return FirebaseTokenRepositoryImpl(appPreferences)
    }
    @Provides
    fun provideSettingRepository(appPreferences: AppPreferences): SettingRepository =
        SettingRepositoryImpl(appPreferences)

}