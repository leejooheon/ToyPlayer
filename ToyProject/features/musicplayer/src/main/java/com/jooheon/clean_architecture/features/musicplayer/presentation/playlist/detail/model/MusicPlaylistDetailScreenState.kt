package com.jooheon.clean_architecture.features.musicplayer.presentation.playlist.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist

data class MusicPlaylistDetailScreenState(
    val playlist: Playlist
) {
    companion object {
        val default = MusicPlaylistDetailScreenState(
            playlist = Playlist.default
        )
    }
}