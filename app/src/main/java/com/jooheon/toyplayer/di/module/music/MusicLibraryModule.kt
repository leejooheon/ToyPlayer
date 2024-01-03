package com.jooheon.toyplayer.di.module.music

import androidx.room.Room
import com.jooheon.toyplayer.data.dao.playlist.PlaylistDatabase
import com.jooheon.toyplayer.data.dao.playlist.data.PlaylistMapper
import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.toyplayer.data.repository.library.PlayingQueueRepositoryImpl
import com.jooheon.toyplayer.data.repository.library.PlaylistRepositoryImpl
import com.jooheon.toyplayer.domain.repository.library.PlayingQueueRepository
import com.jooheon.toyplayer.domain.repository.library.PlaylistRepository
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCaseImpl
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCaseImpl
import com.jooheon.toyplayer.di.Constants
import com.jooheon.toyplayer.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicLibraryModule {
    @Provides
    @Singleton
    fun providePlaylistDatabase(myApplication: MyApplication) =
        Room.databaseBuilder(myApplication, PlaylistDatabase::class.java, Constants.PLAYLIST_DB).build()
    @Provides
    @Singleton
    fun provideMusicPlaylistDao(playlistDatabase: PlaylistDatabase) = playlistDatabase.dao
    @Provides
    fun provideMusicPlaylistMapper(): PlaylistMapper = PlaylistMapper()

    @Provides
    fun providePlaylistRepository(
        localPlaylistDataSource: LocalPlaylistDataSource
    ): PlaylistRepository = PlaylistRepositoryImpl(localPlaylistDataSource)

    @Singleton
    @Provides
    fun providePlaylistUseCase(
        applicationScope: CoroutineScope,
        playlistRepository: PlaylistRepository
    ): PlaylistUseCase = PlaylistUseCaseImpl(applicationScope, playlistRepository)

    @Singleton
    @Provides
    fun providePlayingQueueRepository(
        applicationScope: CoroutineScope,
        localPlaylistDataSource: LocalPlaylistDataSource,
        appPreferences: AppPreferences,
    ): PlayingQueueRepository = PlayingQueueRepositoryImpl(
        applicationScope = applicationScope,
        localPlaylistDataSource = localPlaylistDataSource,
        appPreferences = appPreferences
    )

    @Singleton
    @Provides
    fun providePlayingQueueUseCase(
        playingQueueRepository: PlayingQueueRepository
    ): PlayingQueueUseCase = PlayingQueueUseCaseImpl(
        playingQueueRepository
    )
}