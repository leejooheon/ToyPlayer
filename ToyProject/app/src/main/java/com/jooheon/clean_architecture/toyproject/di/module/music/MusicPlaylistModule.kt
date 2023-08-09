package com.jooheon.clean_architecture.toyproject.di.module.music

import androidx.room.Room
import com.jooheon.clean_architecture.data.dao.playlist.PlaylistDatabase
import com.jooheon.clean_architecture.data.dao.playlist.data.PlaylistMapper
import com.jooheon.clean_architecture.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.clean_architecture.data.repository.MusicPlaylistRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.MusicPlaylistRepository
import com.jooheon.clean_architecture.domain.usecase.music.playlist.MusicPlaylistUseCase
import com.jooheon.clean_architecture.toyproject.di.Constants
import com.jooheon.clean_architecture.toyproject.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicPlaylistModule {
    @Provides
    @Singleton
    fun providePlaylistDatabase(myApplication: MyApplication) =
        Room.databaseBuilder(myApplication, PlaylistDatabase::class.java, Constants.PLAYLIST_DB).build()
    @Provides
    @Singleton
    fun provideMusicPlaylistDao(playlistDatabase: PlaylistDatabase) =
        playlistDatabase.dao
    @Provides
    fun provideMusicPlaylistMapper(): PlaylistMapper = PlaylistMapper()
    @Provides
    fun provideMusicPlaylistRepository(
        localPlaylistDataSource: LocalPlaylistDataSource
    ): MusicPlaylistRepository = MusicPlaylistRepositoryImpl(localPlaylistDataSource)

    @Provides
    @Singleton
    fun provideMusicPlaylistUseCase(
        applicationScope: CoroutineScope,
        musicPlaylistRepository: MusicPlaylistRepository
    ): MusicPlaylistUseCase = MusicPlaylistUseCase(
        applicationScope = applicationScope,
        musicPlaylistRepository = musicPlaylistRepository
    )
}