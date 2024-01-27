package com.jooheon.toyplayer.di.module.music

import android.content.Context
import com.jooheon.toyplayer.data.datasource.local.LocalMusicDataSource
import com.jooheon.toyplayer.data.repository.AutomotiveRepositoryImpl
import com.jooheon.toyplayer.domain.repository.AutomotiveRepository
import com.jooheon.toyplayer.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.toyplayer.domain.usecase.music.automotive.AutomotiveUseCaseImpl
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object AutomotiveModule {
    @Provides
    fun provideAutomotiveRepository(
        localMusicDataSource: LocalMusicDataSource
    ): AutomotiveRepository = AutomotiveRepositoryImpl(localMusicDataSource)

    @Provides
    @ServiceScoped
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