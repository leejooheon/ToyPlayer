package com.jooheon.clean_architecture.features.musicservice.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.clean_architecture.features.musicservice.usecase.MediaControllerManager
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides
    @Singleton
    @UnstableApi
    fun provideMediaControllerManager(
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
    ) = MediaControllerManager(
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