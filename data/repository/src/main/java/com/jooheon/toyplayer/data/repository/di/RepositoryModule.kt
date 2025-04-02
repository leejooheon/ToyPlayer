package com.jooheon.toyplayer.data.repository.di

import com.jooheon.toyplayer.data.datastore.DefaultSettingsPreferencesDataSource
import com.jooheon.toyplayer.data.datastore.PlayerSettingsPreferencesDataSource
import com.jooheon.toyplayer.data.equalizer.EqualizerDataSource
import com.jooheon.toyplayer.data.music.LocalMusicDataSource
import com.jooheon.toyplayer.data.music.RemoteEqualizerDataSource
import com.jooheon.toyplayer.data.music.RemoteMusicDataSource
import com.jooheon.toyplayer.data.playlist.PlaylistDataSource
import com.jooheon.toyplayer.data.repository.DefaultSettingsRepositoryImpl
import com.jooheon.toyplayer.data.repository.EqualizerRepositoryImpl
import com.jooheon.toyplayer.data.repository.MusicListRepositoryImpl
import com.jooheon.toyplayer.data.repository.PlayerSettingsRepositoryImpl
import com.jooheon.toyplayer.data.repository.PlaylistRepositoryImpl
import com.jooheon.toyplayer.data.repository.RadioRepositoryImpl
import com.jooheon.toyplayer.domain.repository.api.DefaultSettingsRepository
import com.jooheon.toyplayer.domain.repository.api.EqualizerRepository
import com.jooheon.toyplayer.domain.repository.api.MusicListRepository
import com.jooheon.toyplayer.domain.repository.api.PlayerSettingsRepository
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import com.jooheon.toyplayer.domain.repository.api.RadioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    fun provideDefaultSettingsRepository(
        preferencesDataSource: DefaultSettingsPreferencesDataSource
    ): DefaultSettingsRepository = DefaultSettingsRepositoryImpl(preferencesDataSource)

    @Provides
    fun providePlaybackSettingsRepository(
        preferencesDataSource: PlayerSettingsPreferencesDataSource
    ): PlayerSettingsRepository = PlayerSettingsRepositoryImpl(preferencesDataSource)

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

    @Provides
    fun provideRadioRepository(
        dataSource: RemoteMusicDataSource,
    ): RadioRepository = RadioRepositoryImpl(
        dataSource = dataSource,
    )

    @Provides
    fun provideEqualizerRepository(
        remoteEqualizerDataSource: RemoteEqualizerDataSource,
        equalizerDataSource: EqualizerDataSource,
    ): EqualizerRepository = EqualizerRepositoryImpl(
        remoteEqualizerDataSource = remoteEqualizerDataSource,
        equalizerDataSource = equalizerDataSource,
    )
}
