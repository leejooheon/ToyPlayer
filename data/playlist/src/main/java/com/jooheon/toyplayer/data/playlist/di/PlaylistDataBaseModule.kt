package com.jooheon.toyplayer.data.playlist.di

import android.app.Application
import androidx.room.Room
import com.jooheon.toyplayer.data.playlist.dao.PlaylistDatabase
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
    fun providePlaylistDatabase(application: Application): PlaylistDatabase =
        Room.databaseBuilder(
            application,
            PlaylistDatabase::class.java,
            "playlist_db"
        ).build()

    @Provides
    @Singleton
    fun providePlaylistDao(database: PlaylistDatabase) = database.dao
}