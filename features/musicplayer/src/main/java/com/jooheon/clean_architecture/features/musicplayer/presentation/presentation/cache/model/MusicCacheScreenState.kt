package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.cache.model

import com.jooheon.clean_architecture.domain.entity.music.Song

data class MusicCacheScreenState(
    val songs: List<Song>
) {
    companion object {
        val default = MusicCacheScreenState(
            songs = Song.defaultList,
        )
    }
}