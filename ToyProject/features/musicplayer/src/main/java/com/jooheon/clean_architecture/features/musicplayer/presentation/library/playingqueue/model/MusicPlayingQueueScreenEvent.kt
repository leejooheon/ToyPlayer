package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue.model

import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicPlayingQueueScreenEvent {
    object OnBackClick: MusicPlayingQueueScreenEvent()
    data class OnActionPlayAll(
        val shuffle: Boolean
    ): MusicPlayingQueueScreenEvent()
    data class OnSongClick(val song: Song): MusicPlayingQueueScreenEvent()
    data class OnDeleteClick(val song: Song): MusicPlayingQueueScreenEvent()
}