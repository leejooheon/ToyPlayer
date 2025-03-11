package com.jooheon.toyplayer.features.playlist.details.model

import com.jooheon.toyplayer.domain.model.music.Playlist

data class PlaylistDetailUiState(
    val playlist: Playlist
) {
    companion object {
        val default = PlaylistDetailUiState(
            playlist = Playlist.default
        )

        val preview = PlaylistDetailUiState(
            playlist = Playlist.preview
        )
    }
}