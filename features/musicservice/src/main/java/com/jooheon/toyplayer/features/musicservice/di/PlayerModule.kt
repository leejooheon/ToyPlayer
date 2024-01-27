package com.jooheon.toyplayer.features.musicservice.di

import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackListener
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
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
    ) = PlaybackListener(
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    )

    @Provides
    @Singleton
    @UnstableApi
    fun providePlaybackUriResolver(
        musicStateHolder: MusicStateHolder
    ): PlaybackUriResolver = PlaybackUriResolver(musicStateHolder)

    @Provides
    @Singleton
    fun provideMusicStateHolder(
        applicationScope: CoroutineScope,
    ) = MusicStateHolder(applicationScope)
}