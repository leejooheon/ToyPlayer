package com.jooheon.toyplayer.features.library.main.model

import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.Playlist

data class LibraryUiState(
    val playlists: List<Playlist>,
    val artists: List<Artist>,
) {
    companion object {
        val default = LibraryUiState(
            playlists = emptyList(),
            artists = emptyList(),
        )
        val preview = LibraryUiState(
            playlists = listOf(Playlist.preview),
            artists = listOf(Artist.default, Artist.default, Artist.default),
        )
    }
}