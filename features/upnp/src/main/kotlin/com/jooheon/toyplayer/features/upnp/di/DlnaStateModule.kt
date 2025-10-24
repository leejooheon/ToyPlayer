package com.jooheon.toyplayer.features.upnp.di

import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.features.upnp.DlnaStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DlnaStateModule {
    @Singleton
    @Provides
    fun provideDlnaStateHolder(): DlnaStateHolder = DlnaStateHolder()

    @Provides
    fun provideCastStateHolder(
        dlnaStateHolder: DlnaStateHolder
    ): CastStateHolder = dlnaStateHolder
}