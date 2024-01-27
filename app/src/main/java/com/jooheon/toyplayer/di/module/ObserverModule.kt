package com.jooheon.toyplayer.di.module

import android.content.Context
import com.jooheon.toyplayer.data.datasource.local.NetworkConnectivity
import com.jooheon.toyplayer.domain.observer.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ObserverModule {
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext applicationContext: Context
    ): NetworkConnectivityObserver = NetworkConnectivity(applicationContext)
}