package com.jooheon.toyplayer.di.module.music

import android.content.Context
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

@Module
@InstallIn(ActivityRetainedComponent::class)
object PlayerModule {
    @Provides
    @ActivityRetainedScoped
    fun providePlayerController(
        @ApplicationContext application: Context,
        applicationScope: CoroutineScope,
        musicStateHolder: MusicStateHolder, // 지우자.
        activityRetainedLifecycle: ActivityRetainedLifecycle
    ) = PlayerController(
        applicationScope = applicationScope,
        musicStateHolder = musicStateHolder,
    ).also {
        it.connect(application)
        activityRetainedLifecycle.addOnClearedListener {
            it.release()
        }
    }
}