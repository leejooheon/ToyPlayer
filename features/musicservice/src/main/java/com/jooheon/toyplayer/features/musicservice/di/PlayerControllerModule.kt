package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.ViewModelLifecycle
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
object PlayerControllerModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MediaControllerCoroutineScope

    @MediaControllerCoroutineScope
    @ViewModelScoped
    @Provides
    fun provideViewModelScope(
        viewModelLifecycle: ViewModelLifecycle,
    ): CoroutineScope {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        viewModelLifecycle.addOnClearedListener {
            scope.cancel()
        }
        return scope
    }

    @ViewModelScoped
    @Provides
    fun providePlayerController(
        @ApplicationContext application: Context,
        @MediaControllerCoroutineScope scope: CoroutineScope,
        viewModelLifecycle: ViewModelLifecycle
    ) = PlayerController(scope).also {
        it.connect(application)
        viewModelLifecycle.addOnClearedListener {
            it.release()
        }
    }
}