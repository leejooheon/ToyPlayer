package com.jooheon.toyplayer.features.upnp.di

import android.content.Context
import com.jooheon.toyplayer.core.system.network.WifiConnectivity
import com.jooheon.toyplayer.domain.castapi.CastController
import com.jooheon.toyplayer.domain.castapi.CastService
import com.jooheon.toyplayer.domain.castapi.CastStateHolder
import com.jooheon.toyplayer.features.common.temp.MusicServiceContext
import com.jooheon.toyplayer.features.common.temp.MusicServiceCoroutineScope
import com.jooheon.toyplayer.features.upnp.DlnaPlayerController
import com.jooheon.toyplayer.features.upnp.DlnaServiceManager
import com.jooheon.toyplayer.features.upnp.DlnaStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(ServiceComponent::class)
object DlnaModule {

    @ServiceScoped
    @Provides
    fun provideDlnaPlayerController(
        @MusicServiceContext context: Context,
        @MusicServiceCoroutineScope scope: CoroutineScope,
        dlnaStateHolder: DlnaStateHolder,
    ): DlnaPlayerController = DlnaPlayerController(
        context = context,
        scope = scope,
        dlnaStateHolder = dlnaStateHolder
    )

    @Provides
    fun provideCastController(
        dlnaPlayerController: DlnaPlayerController
    ): CastController = dlnaPlayerController

    @ServiceScoped
    @Provides
    fun provideDlnaServiceManager(
        @MusicServiceContext context: Context,
        @MusicServiceCoroutineScope scope: CoroutineScope,
        dlnaStateHolder: DlnaStateHolder,
        dlnaPlayerController: DlnaPlayerController,
        wifiConnectivity: WifiConnectivity,
    ): DlnaServiceManager = DlnaServiceManager(
        context = context,
        scope = scope,
        dlnaStateHolder = dlnaStateHolder,
        dlnaPlayerController = dlnaPlayerController,
        wifiConnectivity = wifiConnectivity,
    )

    @Provides
    fun provideCastManager(
        dlnaServiceManager: DlnaServiceManager
    ): CastService = dlnaServiceManager
}