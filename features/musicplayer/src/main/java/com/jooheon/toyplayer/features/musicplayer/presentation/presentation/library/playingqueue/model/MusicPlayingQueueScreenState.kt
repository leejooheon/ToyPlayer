package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model

import com.jooheon.toyplayer.domain.entity.music.Playlist

data class MusicPlayingQueueScreenState(
    val playlist: Playlist
) {
    companion object {
        val default = MusicPlayingQueueScreenState(
            playlist = Playlist.default
        )
    }
}