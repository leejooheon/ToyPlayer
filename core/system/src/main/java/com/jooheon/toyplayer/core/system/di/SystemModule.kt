package com.jooheon.toyplayer.core.system.di

import android.content.Context
import com.jooheon.toyplayer.core.system.audio.AudioOutputObserver
import com.jooheon.toyplayer.core.system.audio.AudioOutputObserverImpl
import com.jooheon.toyplayer.core.system.network.NetworkConnectivity
import com.jooheon.toyplayer.core.system.network.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SystemModule {
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
    ): NetworkConnectivityObserver = NetworkConnectivity(context)

    @Provides
    fun provideAudioObserver(
        @ApplicationContext context: Context
    ): AudioOutputObserver = AudioOutputObserverImpl(context)
}