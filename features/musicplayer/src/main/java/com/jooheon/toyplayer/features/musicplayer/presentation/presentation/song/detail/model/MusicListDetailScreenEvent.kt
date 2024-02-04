package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail.model

import com.jooheon.toyplayer.domain.entity.music.MusicListType


sealed class MusicListDetailScreenEvent {
    data class OnMusicListTypeChanged(val musicListType: MusicListType) : MusicListDetailScreenEvent()
}