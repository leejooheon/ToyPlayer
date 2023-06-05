package com.jooheon.clean_architecture.toyproject.di.module.music

import androidx.room.Room
import com.jooheon.clean_architecture.data.dao.playlist.PlaylistDatabase
import com.jooheon.clean_architecture.data.dao.playlist.data.PlaylistMapper
import com.jooheon.clean_architecture.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.clean_architecture.data.repository.PlaylistRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.PlaylistRepository
import com.jooheon.clean_architecture.domain.usecase.playlist.PlaylistUseCase
import com.jooheon.clean_architecture.domain.usecase.playlist.PlaylistUseCaseImpl
import com.jooheon.clean_architecture.toyproject.di.Constants
import com.jooheon.clean_architecture.toyproject.di.MyApplication
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
    fun providePlaylistDatabase(myApplication: MyApplication) =
        Room.databaseBuilder(myApplication, PlaylistDatabase::class.java, Constants.PLAYLIST_DB).build()
    @Provides
    @Singleton
    fun providePlaylistDao(playlistDatabase: PlaylistDatabase) =
        playlistDatabase.dao
    @Provides
    fun providePlaylistMapper(): PlaylistMapper = PlaylistMapper()
    @Provides
    fun providePlaylistRepository(
        localPlaylistDataSource: LocalPlaylistDataSource
    ): PlaylistRepository = PlaylistRepositoryImpl(localPlaylistDataSource)

    @Provides
    fun providePlaylistUseCase(
        playlistRepository: PlaylistRepository
    ): PlaylistUseCase = PlaylistUseCaseImpl(playlistRepository)
}