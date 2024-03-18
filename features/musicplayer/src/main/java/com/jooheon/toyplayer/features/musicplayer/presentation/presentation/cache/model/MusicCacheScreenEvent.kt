package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.cache.model

import android.content.Context

sealed class MusicCacheScreenEvent {
    data class OnRefresh(val context: Context): MusicCacheScreenEvent()
}