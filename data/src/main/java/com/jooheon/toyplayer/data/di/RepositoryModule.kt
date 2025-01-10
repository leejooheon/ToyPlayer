package com.jooheon.toyplayer.data.di

import com.jooheon.toyplayer.data.datastore.DefaultSettingsPreferencesDataSource
import com.jooheon.toyplayer.data.datastore.PlaybackPreferencesDataSource
import com.jooheon.toyplayer.data.music.LocalMusicDataSource
import com.jooheon.toyplayer.data.music.RemoteMusicDataSource
import com.jooheon.toyplayer.data.playlist.PlaylistDataSource
import com.jooheon.toyplayer.data.repository.DefaultSettingsRepositoryImpl
import com.jooheon.toyplayer.data.repository.MusicListRepositoryImpl
import com.jooheon.toyplayer.data.repository.PlaybackSettingsRepositoryImpl
import com.jooheon.toyplayer.data.repository.PlaylistRepositoryImpl
import com.jooheon.toyplayer.domain.repository.DefaultSettingsRepository
import com.jooheon.toyplayer.domain.repository.MusicListRepository
import com.jooheon.toyplayer.domain.repository.PlaybackSettingsRepository
import com.jooheon.toyplayer.domain.repository.PlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal object RepositoryModule {

    @Provides
    fun provideDefaultSettingsRepository(
        preferencesDataSource: DefaultSettingsPreferencesDataSource
    ): DefaultSettingsRepository = DefaultSettingsRepositoryImpl(preferencesDataSource)

    @Provides
    fun providePlaybackSettingsRepository(
        preferencesDataSource: PlaybackPreferencesDataSource
    ): PlaybackSettingsRepository = PlaybackSettingsRepositoryImpl(preferencesDataSource)

    @Provides
    fun provideMusicListRepository(
        localMusicDataSource: LocalMusicDataSource,
        remoteMusicDataSource: RemoteMusicDataSource,
    ): MusicListRepository = MusicListRepositoryImpl(
        localMusicDataSource = localMusicDataSource,
        remoteMusicDataSource = remoteMusicDataSource,
    )

    @Provides
    fun providePlaylistRepository(
        playlistDataSource: PlaylistDataSource,
    ): PlaylistRepository = PlaylistRepositoryImpl(playlistDataSource)
}
