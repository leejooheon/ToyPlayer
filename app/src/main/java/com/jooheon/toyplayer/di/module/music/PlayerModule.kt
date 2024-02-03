package com.jooheon.toyplayer.di.module.music

import android.content.Context
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ActivityRetainedComponent::class)
object PlayerModule {
    @Provides
    @ActivityRetainedScoped
    fun providePlayerController(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder,
    ) = PlayerController(
        context = context,
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    )
}