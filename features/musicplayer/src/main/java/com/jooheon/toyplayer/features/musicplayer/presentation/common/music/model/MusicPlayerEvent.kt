package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model

import com.jooheon.toyplayer.domain.model.music.Song

sealed class MusicPlayerEvent {
    data class OnSongClick(val song: Song): MusicPlayerEvent()
    data object OnPause: MusicPlayerEvent()
    data object OnNextClick: MusicPlayerEvent()
    data object OnPreviousClick: MusicPlayerEvent()
    data object OnShuffleClick: MusicPlayerEvent()
    data object OnRepeatClick: MusicPlayerEvent()
    data object OnPlayingQueueClick: MusicPlayerEvent()


    data class OnPlayPauseClick(val song: Song): MusicPlayerEvent()
    data class OnSnapTo(val duration: Long): MusicPlayerEvent()
    data class OnEnqueue(
        val songs: List<Song>,
        val shuffle: Boolean,
        val playWhenReady: Boolean,
    ): MusicPlayerEvent()
    data class OnDeleteClick(val song: Song): MusicPlayerEvent()
}