package com.jooheon.toyplayer.di.module.music

import androidx.room.Room
import com.jooheon.toyplayer.MyApplication
import com.jooheon.toyplayer.data.dao.playlist.PlaylistDatabase
import com.jooheon.toyplayer.data.dao.playlist.data.PlaylistMapper
import com.jooheon.toyplayer.di.Constants
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
    fun providePlaylistDatabase(myApplication: MyApplication) =
        Room.databaseBuilder(myApplication, PlaylistDatabase::class.java, Constants.PLAYLIST_DB)
            .build()

    @Provides
    @Singleton
    fun provideMusicPlaylistDao(playlistDatabase: PlaylistDatabase) = playlistDatabase.dao

    @Provides
    fun provideMusicPlaylistMapper(): PlaylistMapper = PlaylistMapper()
}