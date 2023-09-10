package com.jooheon.clean_architecture.toyproject.di.module.music

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.data.datasource.local.LocalMusicDataSource
import com.jooheon.clean_architecture.data.datasource.remote.RemoteMusicDataSource
import com.jooheon.clean_architecture.data.local.AppPreferences
import com.jooheon.clean_architecture.data.repository.music.MusicListRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.MusicListRepository
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicController
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicServiceModule {

    @Provides
    fun provideMusicPlayListRepository(
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
    @Singleton
    fun provideMusicController(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        exoPlayer: ExoPlayer,
    ): MusicController = MusicController(
        context = context,
        applicationScope = applicationScope,
        exoPlayer = exoPlayer,
    )

    @Provides
    @Singleton
    fun provideMusicControllerUsecase(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicController: MusicController,
        playingQueueUseCase: PlayingQueueUseCase,
    ): MusicControllerUsecase = MusicControllerUsecase(
        context = context,
        applicationScope = applicationScope,
        musicController = musicController,
        playingQueueUseCase = playingQueueUseCase,
    )

    @Provides
    @Singleton
    fun provideMediaSessionCallback(
        applicationScope: CoroutineScope,
        musicControllerUsecase: MusicControllerUsecase
    ): MediaSessionCallback = MediaSessionCallback(
        applicationScope = applicationScope,
        musicControllerUsecase = musicControllerUsecase
    )

}