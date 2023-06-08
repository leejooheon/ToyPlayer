package com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.model

import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicPlayerEvent {
    object OnPause: MusicPlayerEvent()
    object OnNextClick: MusicPlayerEvent()
    object OnPreviousClick: MusicPlayerEvent()
    object OnShuffleClick: MusicPlayerEvent()
    object OnRepeatClick: MusicPlayerEvent()

    data class OnPlayPauseClick(val song: Song): MusicPlayerEvent()
    data class OnPlayClick(val song: Song): MusicPlayerEvent()
    data class OnSnapTo(val duration: Long): MusicPlayerEvent()
}