package com.jooheon.clean_architecture.toyproject.di.module.music

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.data.datasource.local.LocalMusicPlayListDataSource
import com.jooheon.clean_architecture.data.datasource.remote.RemoteMusicPlayListDataSource
import com.jooheon.clean_architecture.data.repository.music.MusicPlayListRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.MusicPlayListRepository
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicController
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import com.jooheon.clean_architecture.features.musicservice.usecase.manager.MusicPlayListManager
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
        localMusicPlayListDataSource: LocalMusicPlayListDataSource,
        remoteMusicPlayListDataSource: RemoteMusicPlayListDataSource
    ): MusicPlayListRepository {
        return MusicPlayListRepositoryImpl(
            localMusicPlayListDataSource = localMusicPlayListDataSource,
            remoteMusicPlayListDataSource = remoteMusicPlayListDataSource
        )
    }
    @Provides
    @Singleton
    fun provideMusicController(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        exoPlayer: ExoPlayer,
        musicPlayListManager: MusicPlayListManager,
    ): MusicController = MusicController(
        context = context,
        applicationScope = applicationScope,
        exoPlayer = exoPlayer,
        musicPlayListManager = musicPlayListManager,
    )

    @Provides
    @Singleton
    fun provideMusicControllerUsecase(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicController: MusicController,
    ): MusicControllerUsecase = MusicControllerUsecase(
        context = context,
        applicationScope = applicationScope,
        musicController = musicController,
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