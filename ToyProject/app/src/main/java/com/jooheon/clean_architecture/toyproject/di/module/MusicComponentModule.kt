package com.jooheon.clean_architecture.toyproject.di.module

import android.content.Context
import com.jooheon.clean_architecture.data.datasource.local.MusicDataSource
import com.jooheon.clean_architecture.data.repository.MusicRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.MusicRepository
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlaylistUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicControllerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicComponentModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        musicDataSource: MusicDataSource
    ): MusicRepository {
        return MusicRepositoryImpl(musicDataSource)
    } // 플레이 리스트 접근

    @Provides
    @Singleton
    fun provideMusicUseCase(repository: MusicRepository): MusicUseCase =
        MusicUseCaseImpl(repository) // 플레이 리스트 flow 보유

    @Provides
    @Singleton
    fun providesMusicPlayerUseCase(
        musicUseCase: MusicUseCase
    ): MusicPlaylistUseCase = MusicPlaylistUseCase(musicUseCase) // 플레이 리스트를 MusicController에 제공해줌


    @Provides
    @Singleton
    fun provideMusicController(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicPlaylistUseCase: MusicPlaylistUseCase,
        settingUseCase: SettingUseCase
    ) = MusicController(
        context = context,
        applicationScope = applicationScope,
        musicPlaylistUseCase = musicPlaylistUseCase,
        settingUseCase = settingUseCase
    )

    @Provides
    @Singleton
    fun provideMusicPlayerViewModel(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicController: MusicController,
    ) = MusicControllerUseCase(
        context = context,
        applicationScope = applicationScope,
        musicController = musicController
    )
}