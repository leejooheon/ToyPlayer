package com.jooheon.toyplayer.di.module.music

import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.toyplayer.data.repository.library.PlayingQueueRepositoryImpl
import com.jooheon.toyplayer.domain.repository.library.PlayingQueueRepository
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ServiceComponent::class)
object PlayingQueueModule {
    @Provides
    @ServiceScoped
    fun providePlayingQueueUseCase(
        playingQueueRepository: PlayingQueueRepository
    ): PlayingQueueUseCase = PlayingQueueUseCaseImpl(
        playingQueueRepository
    )
    
    @Provides
    fun providePlayingQueueRepository(
        applicationScope: CoroutineScope,
        localPlaylistDataSource: LocalPlaylistDataSource,
        appPreferences: AppPreferences,
    ): PlayingQueueRepository = PlayingQueueRepositoryImpl(
        applicationScope = applicationScope,
        localPlaylistDataSource = localPlaylistDataSource,
        appPreferences = appPreferences
    )
}