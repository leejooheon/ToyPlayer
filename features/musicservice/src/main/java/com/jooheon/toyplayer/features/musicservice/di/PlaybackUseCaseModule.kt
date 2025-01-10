package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import com.jooheon.toyplayer.domain.observer.NetworkConnectivityObserver
import com.jooheon.toyplayer.domain.usecase.PlaybackSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackErrorUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackLogUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object PlaybackUseCaseModule {
    @Provides
    @ServiceScoped
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
    @ServiceScoped
    fun providePlaybackUseCase(
        musicStateHolder: MusicStateHolder,
        playlistUseCase: PlaylistUseCase,
        playbackSettingsUseCase: PlaybackSettingsUseCase,
    ): PlaybackUseCase = PlaybackUseCase(
        musicStateHolder = musicStateHolder,
        playlistUseCase = playlistUseCase,
        playbackSettingsUseCase = playbackSettingsUseCase,
    )

    @Provides
    @ServiceScoped
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