package com.jooheon.toyplayer.features.splash.model

sealed interface SplashState {
    data object Done : SplashState
}