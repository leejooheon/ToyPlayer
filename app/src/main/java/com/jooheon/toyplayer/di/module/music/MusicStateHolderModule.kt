@file:JvmName("MusicServiceModuleKt")

package com.jooheon.toyplayer.di.module.music

import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicStateHolderModule {
    @Provides
    @Singleton
    fun provideMusicStateHolder(
        applicationScope: CoroutineScope,
    ) = MusicStateHolder(applicationScope)
}