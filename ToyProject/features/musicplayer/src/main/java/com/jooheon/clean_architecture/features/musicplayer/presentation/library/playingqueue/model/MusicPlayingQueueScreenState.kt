package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist

data class MusicPlayingQueueScreenState(
    val playlist: Playlist
) {
    companion object {
        val default = MusicPlayingQueueScreenState(
            playlist = Playlist.default
        )
    }
}