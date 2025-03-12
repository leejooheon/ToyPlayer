package com.jooheon.toyplayer.features.splash.model

sealed interface SplashState {
    data object Default : SplashState
    data class NetworkAvailable(val value: Boolean) : SplashState
    data class ServiceAvailable(val value: Boolean) : SplashState
    data object Update : SplashState
    data object Done : SplashState
}