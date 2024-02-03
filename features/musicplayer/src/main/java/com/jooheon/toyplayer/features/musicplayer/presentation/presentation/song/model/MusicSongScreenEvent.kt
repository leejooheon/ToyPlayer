package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model

import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent

sealed class MusicSongScreenEvent {
    data class OnMusicListTypeChanged(val musicListType: MusicListType): MusicSongScreenEvent()
    data class OnItemViewTypeChanged(val type: Boolean): MusicSongScreenEvent()
    data object ReloadSongList: MusicSongScreenEvent()
}