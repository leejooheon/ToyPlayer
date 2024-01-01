package com.jooheon.clean_architecture.toyproject.di.module.music

import android.content.Context
import com.jooheon.clean_architecture.data.datasource.local.LocalMusicDataSource
import com.jooheon.clean_architecture.data.repository.AutomotiveRepositoryImpl
import com.jooheon.clean_architecture.domain.repository.AutomotiveRepository
import com.jooheon.clean_architecture.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.clean_architecture.domain.usecase.music.automotive.AutomotiveUseCaseImpl
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.features.musicservice.data.MediaItemProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AutomotiveModule {
    @Provides
    fun provideAutomotiveRepository(
        localMusicDataSource: LocalMusicDataSource
    ): AutomotiveRepository = AutomotiveRepositoryImpl(localMusicDataSource)

    @Provides
    @Singleton
    fun provideAutomotiveUseCase(
        automotiveRepository: AutomotiveRepository,
        musicListUseCase: MusicListUseCase,
    ): AutomotiveUseCase = AutomotiveUseCaseImpl(automotiveRepository, musicListUseCase)

    @Provides
    fun provideMediaItemProviderProvider(
        @ApplicationContext context: Context,
        automotiveUseCase: AutomotiveUseCase,
    ): MediaItemProvider = MediaItemProvider(context, automotiveUseCase)
}