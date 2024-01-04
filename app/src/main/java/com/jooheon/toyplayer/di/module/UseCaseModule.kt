package com.jooheon.toyplayer.di.module

import com.jooheon.toyplayer.domain.repository.*
import com.jooheon.toyplayer.domain.usecase.firebase.FirebaseTokenUseCase
import com.jooheon.toyplayer.domain.usecase.firebase.FirebaseTokenUseCaseImpl
import com.jooheon.toyplayer.domain.usecase.setting.SettingUseCase
import com.jooheon.toyplayer.domain.usecase.setting.SettingUseCaseImpl
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
    fun provideFirebaseTokenUseCase(firebaseTokenRepository: FirebaseTokenRepository): FirebaseTokenUseCase =
        FirebaseTokenUseCaseImpl(firebaseTokenRepository)

    @Provides
    @Singleton
    fun provideSettingUseCase(settingRepository: SettingRepository): SettingUseCase =
        SettingUseCaseImpl(settingRepository)
}