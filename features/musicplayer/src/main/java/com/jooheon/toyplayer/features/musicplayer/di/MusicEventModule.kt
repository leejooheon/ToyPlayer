package com.jooheon.toyplayer.features.musicplayer.di

import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object MusicEventModule {
    @Provides
    fun providePlaybackEventUseCase(
        musicStateHolder: MusicStateHolder,
    ) = PlaybackEventUseCase(musicStateHolder)
}