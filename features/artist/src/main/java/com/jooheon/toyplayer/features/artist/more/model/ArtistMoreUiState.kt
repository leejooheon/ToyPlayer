package com.jooheon.toyplayer.features.artist.more.model

import com.jooheon.toyplayer.domain.model.music.Artist

data class ArtistMoreUiState(
    val artists: List<Artist>,
) {
    companion object {
        val default = ArtistMoreUiState(
            artists = emptyList(),
        )
        val preview = ArtistMoreUiState(
            artists = listOf(
                Artist.default, Artist.default, Artist.default, Artist.default
            )
        )
    }
}