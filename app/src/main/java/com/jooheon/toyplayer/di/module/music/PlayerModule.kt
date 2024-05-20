package com.jooheon.toyplayer.di.module.music

import android.content.Context
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ActivityRetainedLifecycle
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ActivityRetainedComponent::class)
object PlayerModule {
    @Provides
    @ActivityRetainedScoped
    fun providePlayerController(
        @ApplicationContext application: Context,
        applicationScope: CoroutineScope,
        activityRetainedLifecycle: ActivityRetainedLifecycle
    ) = PlayerController(applicationScope).also {
        it.connect(application)
        activityRetainedLifecycle.addOnClearedListener {
            it.release()
        }
    }
}