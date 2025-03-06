package com.jooheon.toyplayer.core.network.di

import android.content.Context
import com.jooheon.toyplayer.core.network.NetworkConnectivity
import com.jooheon.toyplayer.core.network.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkConnectivityModule {
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
    ): NetworkConnectivityObserver = NetworkConnectivity(context)
}