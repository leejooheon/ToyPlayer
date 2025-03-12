package com.jooheon.toyplayer.features.splash.model

import android.content.Context

sealed interface SplashEvent {
    data object CheckNetwork: SplashEvent
    data object ServiceCheck: SplashEvent
    data class Update(val context: Context): SplashEvent
}