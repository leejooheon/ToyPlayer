package com.jooheon.clean_architecture.features.musicservice.di

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MusicControllerModule {
    @Provides
    @Singleton
    @UnstableApi
    fun provideMusicController(
        applicationScope: CoroutineScope,
        exoPlayer: ExoPlayer,
    ) = MusicController(
        applicationScope = applicationScope,
        exoPlayer = exoPlayer,
    )
}