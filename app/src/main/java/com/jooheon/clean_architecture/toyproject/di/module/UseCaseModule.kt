package com.jooheon.clean_architecture.toyproject.di.module

import com.jooheon.clean_architecture.domain.repository.*
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCase
import com.jooheon.clean_architecture.domain.usecase.firebase.FirebaseTokenUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCaseImpl
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