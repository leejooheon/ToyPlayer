package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model

import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicPlayerEvent {
    data object OnPlayingQueueClick: MusicPlayerEvent()
    data object OnPause: MusicPlayerEvent()
    data object OnNextClick: MusicPlayerEvent()
    data object OnPreviousClick: MusicPlayerEvent()
    data object OnShuffleClick: MusicPlayerEvent()
    data object OnRepeatClick: MusicPlayerEvent()

    data class OnPlayPauseClick(val song: Song): MusicPlayerEvent()
    data class OnPlayClick(val song: Song): MusicPlayerEvent()
    data class OnSnapTo(val duration: Long): MusicPlayerEvent()
}