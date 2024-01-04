package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.model

sealed class MusicCacheScreenEvent {
    data object PlaceHolder: MusicCacheScreenEvent()
    data object OnRefresh: MusicCacheScreenEvent()
}