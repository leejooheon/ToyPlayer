package com.jooheon.toyplayer.data.playlist.di

import android.app.Application
import androidx.room.Room
import com.jooheon.toyplayer.data.playlist.dao.PlaylistDatabase
import com.jooheon.toyplayer.domain.model.common.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlaylistDataBaseModule {
    @Provides
    @Singleton
    fun providePlaylistDatabase(application: Application) = Room.databaseBuilder(
        application,
        PlaylistDatabase::class.java,
        Constants.PLAYLIST_DB
    ).build()

    @Provides
    @Singleton
    fun provideMusicPlaylistDao(playlistDatabase: PlaylistDatabase) = playlistDatabase.dao
}