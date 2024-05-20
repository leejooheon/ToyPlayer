package com.jooheon.toyplayer.features.musicplayer.di

import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaylistEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.SongItemEventUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MusicEventModule {
    @Provides
    fun providePlaybackEventUseCase(
        musicStateHolder: MusicStateHolder,
    ) = PlaybackEventUseCase(musicStateHolder)

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