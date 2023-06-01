package com.jooheon.clean_architecture.features.musicplayer.model

import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicPlayerScreenEvent {
    object OnPause: MusicPlayerScreenEvent()
    object OnNextClick: MusicPlayerScreenEvent()
    object OnPreviousClick: MusicPlayerScreenEvent()
    object OnShuffleClick: MusicPlayerScreenEvent()
    object OnRepeatClick: MusicPlayerScreenEvent()

    data class OnPlayPauseClick(val song: Song): MusicPlayerScreenEvent()
    data class OnPlayClick(val song: Song): MusicPlayerScreenEvent()
    data class OnSnapTo(val duration: Long): MusicPlayerScreenEvent()
}