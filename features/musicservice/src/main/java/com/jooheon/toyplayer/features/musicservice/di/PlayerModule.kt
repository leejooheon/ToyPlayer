package com.jooheon.toyplayer.features.musicservice.di

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackListener
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@OptIn(UnstableApi::class)
@Module
@InstallIn(ServiceComponent::class)
object PlayerModule {
    @Provides
    @ServiceScoped
    fun providePlaybackListener(
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
    ) = PlaybackListener(
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    )

    @Provides
    @ServiceScoped
    fun providePlaybackUriResolver(
        musicStateHolder: MusicStateHolder
    ): PlaybackUriResolver = PlaybackUriResolver(musicStateHolder)
}