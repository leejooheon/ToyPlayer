package com.jooheon.toyplayer.di.module.music

import com.jooheon.toyplayer.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.toyplayer.data.repository.library.PlaylistRepositoryImpl
import com.jooheon.toyplayer.domain.repository.library.PlaylistRepository
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ActivityRetainedComponent::class)
object PlaylistModule {
    @Provides
    @ActivityRetainedScoped
    fun providePlaylistUseCase(
        applicationScope: CoroutineScope,
        playlistRepository: PlaylistRepository
    ): PlaylistUseCase = PlaylistUseCaseImpl(applicationScope, playlistRepository)

    @Provides
    fun providePlaylistRepository(
        localPlaylistDataSource: LocalPlaylistDataSource
    ): PlaylistRepository = PlaylistRepositoryImpl(localPlaylistDataSource)
}
