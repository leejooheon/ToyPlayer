package com.jooheon.toyplayer.features.library.main.model

import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.Playlist

data class LibraryUiState(
    val defaultPlaylists: List<Playlist>,
    val artists: List<Artist>,
) {
    companion object {
        val default = LibraryUiState(
            defaultPlaylists = emptyList(),
            artists = emptyList(),
        )
        val preview = LibraryUiState(
            defaultPlaylists = listOf(Playlist.preview),
            artists = listOf(Artist.default, Artist.default, Artist.default),
        )
    }
}