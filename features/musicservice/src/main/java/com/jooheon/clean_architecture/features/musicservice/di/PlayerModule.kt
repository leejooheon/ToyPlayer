package com.jooheon.clean_architecture.features.musicservice.di

import androidx.media3.common.util.UnstableApi
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicPlayerListener
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
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
    ): PlaybackUriResolver = PlaybackUriResolver(playingQueueUseCase)

    @Provides
    @Singleton
    fun provideMusicStateHolder(
        applicationScope: CoroutineScope,
        playingQueueUseCase: PlayingQueueUseCase,
    ) = MusicStateHolder(applicationScope, playingQueueUseCase)
}