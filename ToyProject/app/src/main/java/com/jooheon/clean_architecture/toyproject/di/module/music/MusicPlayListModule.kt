package com.jooheon.clean_architecture.toyproject.di.module.music

import com.jooheon.clean_architecture.domain.repository.MusicPlayListRepository
import com.jooheon.clean_architecture.domain.usecase.music.MusicPlayListUsecase
import com.jooheon.clean_architecture.domain.usecase.music.MusicPlayListUsecaseImpl
import com.jooheon.clean_architecture.features.musicservice.usecase.manager.MusicPlayListManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicPlayListModule {

    @Provides
    fun provideMusicPlayListUsecase(repository: MusicPlayListRepository): MusicPlayListUsecase =
        MusicPlayListUsecaseImpl(repository)


    @Provides
    @Singleton
    fun providesMusicPlayListManager(
        applicationScope: CoroutineScope,
        musicPlayListUsecase: MusicPlayListUsecase,
    ): MusicPlayListManager =
        MusicPlayListManager(
            applicationScope = applicationScope,
            musicPlayListUsecase = musicPlayListUsecase,
        )
}