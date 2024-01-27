package com.jooheon.toyplayer.di.module.music

import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object DispatcherModule {
    @Provides
    @ActivityRetainedScoped
    fun providesMusicMediaItemEventUseCase( // TODO: 이름 바꾸자
        playlistUseCase: PlaylistUseCase,
    ) = MusicMediaItemEventUseCase(playlistUseCase)
}
