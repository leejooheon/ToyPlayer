package com.jooheon.toyplayer.features.musicservice.di

import android.app.Service
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MusicServiceContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class MusicServiceCoroutineScope

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceContextModule {
    @Binds
    @MusicServiceContext
    abstract fun bindContext(service: Service): Context
}

@Module
@InstallIn(ServiceComponent::class)
object MusicServiceModule {
    @MusicServiceCoroutineScope
    @ServiceScoped
    @Provides
    fun provideMusicServiceCoroutineScope() = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}