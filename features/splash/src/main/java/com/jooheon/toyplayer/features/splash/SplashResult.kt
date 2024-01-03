package com.jooheon.toyplayer.features.splash

sealed class SplashResult<out T> {
    object Default : SplashResult<Nothing>()
    class NetworkAvailable<out T>(val value: T) : SplashResult<T>()
    object ServiceAvailable : SplashResult<Boolean>()
    object Update : SplashResult<Boolean>()
    class Account<out T>(val value: T) : SplashResult<T>()
    object Permisison : SplashResult<Boolean>()
    class Tutorial<out T>(val value: T) : SplashResult<T>()
    object Done : SplashResult<Nothing>()
}