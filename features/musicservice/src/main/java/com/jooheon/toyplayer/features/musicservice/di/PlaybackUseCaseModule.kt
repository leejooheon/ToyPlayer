package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import com.jooheon.toyplayer.core.system.network.NetworkConnectivityObserver
import com.jooheon.toyplayer.domain.castapi.CastService
import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.temp.MusicServiceCoroutineScope
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.usecase.CastUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackErrorUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackLogUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

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
        @MusicServiceCoroutineScope scope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
        playlistUseCase: PlaylistUseCase,
        playerSettingsUseCase: PlayerSettingsUseCase,
        defaultSettingsUseCase: DefaultSettingsUseCase,
    ): PlaybackUseCase = PlaybackUseCase(
        scope = scope,
        musicStateHolder = musicStateHolder,
        playlistUseCase = playlistUseCase,
        playerSettingsUseCase = playerSettingsUseCase,
        defaultSettingsUseCase = defaultSettingsUseCase,
    )

    @Provides
    @ServiceScoped
    fun providePlaybackErrorUseCase(
        musicStateHolder: MusicStateHolder,
    ): PlaybackErrorUseCase = PlaybackErrorUseCase(
        musicStateHolder = musicStateHolder,
    )

    @Provides
    @ServiceScoped
    fun provideCastUseCase(
        musicStateHolder: MusicStateHolder,
        castService: CastService,
        castStateHolder: CastStateHolder,
    ): CastUseCase = CastUseCase(
        castService = castService,
        castStateHolder = castStateHolder,
        musicStateHolder = musicStateHolder,
    )
}