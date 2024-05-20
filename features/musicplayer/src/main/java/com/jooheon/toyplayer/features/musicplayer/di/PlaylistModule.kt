package com.jooheon.toyplayer.features.musicplayer.di

import com.jooheon.toyplayer.domain.repository.library.PlaylistRepository
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlaylistModule {
    @Provides
    @Singleton
    fun providePlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): PlaylistUseCase = PlaylistUseCaseImpl(playlistRepository)
}