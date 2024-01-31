package com.jooheon.toyplayer.di.module.music

import android.content.Context
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaylistEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.SongItemEventUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ViewModelComponent::class)
object MusicEventModule {
    @Provides
    @ViewModelScoped
    fun providePlayerController(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
    ) = PlayerController(
        context = context,
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    )

    @Provides
    fun providePlaybackEventUseCase(
        playerController: PlayerController,
        musicStateHolder: MusicStateHolder,
    ) = PlaybackEventUseCase(playerController, musicStateHolder)

    @Provides
    fun providePlaylistEventUseCase(
        playlistUseCase: PlaylistUseCase
    ) = PlaylistEventUseCase(playlistUseCase)

    @Provides
    @ViewModelScoped
    fun provideSongItemEventUseCase(
        playlistUseCase: PlaylistUseCase
    ) = SongItemEventUseCase(playlistUseCase)
}