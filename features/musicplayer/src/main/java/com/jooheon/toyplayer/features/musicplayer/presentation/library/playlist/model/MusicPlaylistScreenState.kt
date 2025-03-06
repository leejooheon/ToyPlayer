package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.model

import com.jooheon.toyplayer.domain.model.music.Playlist


data class MusicPlaylistScreenState(
    val playlists: List<Playlist>
) {
    companion object {
        val default = MusicPlaylistScreenState(
            playlists = listOf(Playlist.default)
        )
    }
}