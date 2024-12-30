package com.jooheon.toyplayer.features.musicplayer.presentation.cache.model

import com.jooheon.toyplayer.domain.entity.music.Song

data class MusicCacheScreenState(
    val songs: List<Song>
) {
    companion object {
        val default = MusicCacheScreenState(
            songs = emptyList(),
        )
    }
}