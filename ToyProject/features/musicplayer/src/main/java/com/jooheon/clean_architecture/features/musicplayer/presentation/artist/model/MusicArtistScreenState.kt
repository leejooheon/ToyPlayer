package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model

import com.jooheon.clean_architecture.domain.entity.music.Artist

data class MusicArtistScreenState(
    val artists: List<Artist>,
) {
    companion object {
        val default = MusicArtistScreenState(
            artists = Artist.defaultList,
        )
    }
}