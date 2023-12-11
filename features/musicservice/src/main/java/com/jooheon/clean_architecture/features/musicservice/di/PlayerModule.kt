package com.jooheon.clean_architecture.features.musicservice.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.jooheon.clean_architecture.features.musicservice.notification.CustomMediaSessionCallback
import com.jooheon.clean_architecture.features.musicservice.usecase.MediaControllerManager
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides
    @Singleton
    @UnstableApi
    fun provideMediaControllerManager(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
    ) = MediaControllerManager(
        context = context,
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    )

    @Provides
    @Singleton
    fun provideMusicStateHolder() = MusicStateHolder()
}