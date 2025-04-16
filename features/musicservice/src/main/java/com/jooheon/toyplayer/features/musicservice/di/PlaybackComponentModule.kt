package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import com.jooheon.toyplayer.domain.usecase.MusicListUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.RadioUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.playback.HlsPlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object PlaybackComponentModule {
    @Provides
    @ServiceScoped
    fun providePlaybackListener(
        musicStateHolder: MusicStateHolder,
    ) = PlaybackListener(musicStateHolder)

    @Provides
    @ServiceScoped
    fun provideHlsPlaybackUriResolver(
        radioUseCase: RadioUseCase,
    ): HlsPlaybackUriResolver = HlsPlaybackUriResolver(radioUseCase)

    @Provides
    @ServiceScoped
    fun providePlaybackCacheManager(
        @MusicServiceContext context: Context
    ): PlaybackCacheManager = PlaybackCacheManager(context)

    @Provides
    fun provideMediaItemProvider(
        @ApplicationContext context: Context,
        musicListUseCase: MusicListUseCase,
        radioUseCase: RadioUseCase,
        playlistUseCase: PlaylistUseCase,
    ): MediaItemProvider = MediaItemProvider(
        context = context,
        musicListUseCase = musicListUseCase,
        radioUseCase = radioUseCase,
        playlistUseCase = playlistUseCase
    )
}