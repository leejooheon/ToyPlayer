package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model

import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicPlayingQueueScreenEvent {
    data object OnBackClick: MusicPlayingQueueScreenEvent()
    data class OnPlayAllClick(
        val shuffle: Boolean
    ): MusicPlayingQueueScreenEvent()
    data class OnSongClick(val song: Song): MusicPlayingQueueScreenEvent()
    data class OnDeleteClick(val song: Song): MusicPlayingQueueScreenEvent()
}