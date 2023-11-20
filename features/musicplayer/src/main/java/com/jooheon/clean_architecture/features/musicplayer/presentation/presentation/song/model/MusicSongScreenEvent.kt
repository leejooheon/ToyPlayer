package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.song.model

import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicSongScreenEvent {
    data class OnMusicListTypeChanged(val musicListType: MusicListType): MusicSongScreenEvent()
    data class OnItemViewTypeChanged(val type: Boolean): MusicSongScreenEvent()
}