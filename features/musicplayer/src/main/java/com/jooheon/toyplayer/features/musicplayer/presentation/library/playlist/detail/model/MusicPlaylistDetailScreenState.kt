package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.model

import com.jooheon.toyplayer.domain.model.music.Playlist

data class MusicPlaylistDetailScreenState(
    val playlist: Playlist
) {
    companion object {
        val default = MusicPlaylistDetailScreenState(
            playlist = Playlist.default
        )
    }
}