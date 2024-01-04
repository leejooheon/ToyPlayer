package com.jooheon.toyplayer.features.musicservice.di

import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.usecase.MusicPlayerListener
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides
    @Singleton
    @UnstableApi
    fun provideMusicPlayerListener(
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
    ) = MusicPlayerListener(
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    )

    @Provides
    @Singleton
    @UnstableApi
    fun providePlaybackUriResolver(
        playingQueueUseCase: PlayingQueueUseCase,
        automotiveUseCase: AutomotiveUseCase,
    ): PlaybackUriResolver = PlaybackUriResolver(playingQueueUseCase, automotiveUseCase)

    @Provides
    @Singleton
    fun provideMusicStateHolder(
        applicationScope: CoroutineScope,
        playingQueueUseCase: PlayingQueueUseCase,
    ) = MusicStateHolder(applicationScope, playingQueueUseCase)
}