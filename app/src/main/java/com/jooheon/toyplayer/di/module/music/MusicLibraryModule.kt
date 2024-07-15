package com.jooheon.toyplayer.di.module.music

import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.data.datasource.local.LocalMusicDataSource
import com.jooheon.toyplayer.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.toyplayer.data.datasource.remote.RemoteMusicDataSource
import com.jooheon.toyplayer.data.repository.MusicListRepositoryImpl
import com.jooheon.toyplayer.data.repository.library.PlayingQueueRepositoryImpl
import com.jooheon.toyplayer.data.repository.library.PlaylistRepositoryImpl
import com.jooheon.toyplayer.domain.repository.MusicListRepository
import com.jooheon.toyplayer.domain.repository.library.PlayingQueueRepository
import com.jooheon.toyplayer.domain.repository.library.PlaylistRepository
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
object MusicLibraryModule {
    @Provides
    fun providesMusicListUseCase(
        musicListRepository: MusicListRepository,
    ): MusicListUseCase = MusicListUseCaseImpl(musicListRepository)

    @Provides
    fun provideMusicListRepository( // 음악 리스트 (local, asset, streaming)
        localMusicDataSource: LocalMusicDataSource,
        remoteMusicDataSource: RemoteMusicDataSource,
        appPreferences: AppPreferences,
    ): MusicListRepository {
        return MusicListRepositoryImpl(
            localMusicDataSource = localMusicDataSource,
            remoteMusicDataSource = remoteMusicDataSource,
            appPreferences = appPreferences,
        )
    }

    @Provides
    fun providePlayingQueueRepository( // 현재 재생중인 음악 정보
        applicationScope: CoroutineScope,
        localPlaylistDataSource: LocalPlaylistDataSource,
        appPreferences: AppPreferences,
    ): PlayingQueueRepository = PlayingQueueRepositoryImpl(
        applicationScope = applicationScope,
        localPlaylistDataSource = localPlaylistDataSource,
        appPreferences = appPreferences
    )

    @Provides
    fun providePlaylistRepository( // 재생목록 리스트
        localPlaylistDataSource: LocalPlaylistDataSource
    ): PlaylistRepository = PlaylistRepositoryImpl(localPlaylistDataSource)
}