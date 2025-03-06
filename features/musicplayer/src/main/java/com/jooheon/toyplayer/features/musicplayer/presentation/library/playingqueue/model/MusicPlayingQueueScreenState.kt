package com.jooheon.toyplayer.features.musicplayer.presentation.library.playingqueue.model

import com.jooheon.toyplayer.domain.model.music.Playlist

data class MusicPlayingQueueScreenState(
    val playlist: Playlist
) {
    companion object {
        val default = MusicPlayingQueueScreenState(
            playlist = Playlist.default
        )
    }
}