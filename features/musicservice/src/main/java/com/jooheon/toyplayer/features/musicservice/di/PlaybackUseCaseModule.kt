package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import com.jooheon.toyplayer.domain.observer.NetworkConnectivityObserver
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackErrorUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackLogUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
object PlaybackUseCaseModule {
    @Provides
    fun providePlaybackLogUseCase(
        @ApplicationContext context: Context,
        musicStateHolder: MusicStateHolder,
        networkConnectivityObserver: NetworkConnectivityObserver
    ): PlaybackLogUseCase = PlaybackLogUseCase(
        context = context,
        musicStateHolder = musicStateHolder,
        networkConnectivityObserver = networkConnectivityObserver,
    )

    @Provides
    fun providePlaybackUseCase(
        musicStateHolder: MusicStateHolder,
        playingQueueUseCase: PlayingQueueUseCase,
    ): PlaybackUseCase = PlaybackUseCase(
        musicStateHolder = musicStateHolder,
        playingQueueUseCase = playingQueueUseCase,
    )

    @Provides
    fun providePlaybackErrorUseCase(
        @ApplicationContext context: Context,
        musicStateHolder: MusicStateHolder,
        networkConnectivityObserver: NetworkConnectivityObserver
    ): PlaybackErrorUseCase = PlaybackErrorUseCase(
        context = context,
        musicStateHolder = musicStateHolder,
        networkConnectivityObserver = networkConnectivityObserver,
    )
}