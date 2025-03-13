package com.jooheon.toyplayer.features.playlist.main.model

import com.jooheon.toyplayer.domain.model.music.Playlist

data class PlaylistUiState(
    val playlists: List<Playlist>
) {
    companion object {
        val default = PlaylistUiState(
            playlists = emptyList()
        )
        val preview = PlaylistUiState(
            playlists = listOf(Playlist.preview)
        )
    }
}