package com.jooheon.clean_architecture.features.musicservice.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        exoPlayer: ExoPlayer,
    ) = MusicController(
        context = context,
        applicationScope = applicationScope,
        exoPlayer = exoPlayer
    )
}