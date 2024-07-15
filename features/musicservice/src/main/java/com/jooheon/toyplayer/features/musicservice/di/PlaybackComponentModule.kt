package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.repository.library.PlayingQueueRepository
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCaseImpl
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackListener
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@OptIn(UnstableApi::class)
@Module
@InstallIn(ServiceComponent::class)
object PlaybackComponentModule {
    @Provides
    @ServiceScoped
    fun providePlayingQueueUseCase(
        playingQueueRepository: PlayingQueueRepository
    ): PlayingQueueUseCase = PlayingQueueUseCaseImpl(
        playingQueueRepository
    )

    @Provides
    @ServiceScoped
    fun providePlaybackListener(
        musicStateHolder: MusicStateHolder,
    ) = PlaybackListener(musicStateHolder)

    @Provides
    @ServiceScoped
    fun providePlaybackUriResolver(
        musicStateHolder: MusicStateHolder,
        playbackCacheManager: PlaybackCacheManager,
    ): PlaybackUriResolver = PlaybackUriResolver(
        musicStateHolder = musicStateHolder,
        playbackCacheManager = playbackCacheManager,
    )

    @Provides
    @ServiceScoped
    fun providePlaybackCacheManager(
        @MusicServiceContext context: Context
    ): PlaybackCacheManager = PlaybackCacheManager(context)

    @Provides
    fun provideMediaItemProvider(
        @ApplicationContext context: Context,
        musicListUseCase: MusicListUseCase,
        playlistUseCase: PlaylistUseCase,
    ): MediaItemProvider = MediaItemProvider(
        context = context,
        musicListUseCase = musicListUseCase,
        playlistUseCase = playlistUseCase
    )
}