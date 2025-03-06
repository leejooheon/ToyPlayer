package com.jooheon.toyplayer.features.musicplayer.presentation.artist.model

import com.jooheon.toyplayer.domain.model.music.Artist

data class MusicArtistScreenState(
    val artists: List<Artist>,
) {
    companion object {
        val default = MusicArtistScreenState(
            artists = emptyList(),
        )
    }
}