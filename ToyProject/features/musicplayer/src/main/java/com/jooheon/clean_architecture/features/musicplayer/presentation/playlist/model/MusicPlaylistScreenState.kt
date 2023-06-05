package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist

data class MusicPlaylistScreenState(
    val playlists: List<Playlist>
) {
    companion object {
        val default = MusicPlaylistScreenState(
            playlists = listOf(Playlist.default)
        )
    }
}