package com.jooheon.toyplayer.features.musicplayer.presentation.cache.model

import com.jooheon.toyplayer.domain.model.music.Song

data class MusicCacheScreenState(
    val songs: List<Song>
) {
    companion object {
        val default = MusicCacheScreenState(
            songs = emptyList(),
        )
    }
}