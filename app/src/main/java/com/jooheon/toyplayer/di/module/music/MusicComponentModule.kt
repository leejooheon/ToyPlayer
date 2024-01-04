@file:JvmName("MusicServiceModuleKt")

package com.jooheon.toyplayer.di.module.music

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.SessionToken
import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.data.datasource.local.LocalMusicDataSource
import com.jooheon.toyplayer.data.datasource.remote.RemoteMusicDataSource
import com.jooheon.toyplayer.data.repository.MusicListRepositoryImpl
import com.jooheon.toyplayer.domain.repository.MusicListRepository
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCaseImpl
import com.jooheon.toyplayer.features.common.PlayerController
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicComponentModule {

    @Provides
    @Singleton
    fun providePlayerController(
        @ApplicationContext context: Context,
    ) = PlayerController(
        context = context,
        sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
    )

    @Provides
    @Singleton
    @UnstableApi
    fun provideMusicControllerUsecase(
        applicationScope: CoroutineScope,
        playingQueueUseCase: PlayingQueueUseCase,
        musicStateHolder: MusicStateHolder,
    ) = MusicControllerUseCase(
        applicationScope = applicationScope,
        playingQueueUseCase = playingQueueUseCase,
        musicStateHolder = musicStateHolder
    )

    @Provides
    @Singleton
    fun providesMusicListUseCase(
        musicListRepository: MusicListRepository,
    ): MusicListUseCase = MusicListUseCaseImpl(musicListRepository)

    @Provides
    fun provideMusicListRepository(
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
}