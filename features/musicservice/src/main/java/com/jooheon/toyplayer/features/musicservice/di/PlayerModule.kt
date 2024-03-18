package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackListener
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@OptIn(UnstableApi::class)
@Module
@InstallIn(ServiceComponent::class)
object PlayerModule {
    @Provides
    @ServiceScoped
    fun providePlaybackListener(
        musicStateHolder: MusicStateHolder,
    ) = PlaybackListener(musicStateHolder)

    @Provides
    @ServiceScoped
    fun providePlaybackUriResolver(
        musicStateHolder: MusicStateHolder
    ): PlaybackUriResolver = PlaybackUriResolver(musicStateHolder)

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