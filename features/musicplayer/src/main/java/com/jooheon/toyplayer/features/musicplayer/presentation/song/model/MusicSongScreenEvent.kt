package com.jooheon.toyplayer.features.musicplayer.presentation.song.model

import android.content.Context
import com.jooheon.toyplayer.domain.model.music.MusicListType

sealed class MusicSongScreenEvent {
    data class OnMusicListTypeChanged(val musicListType: MusicListType): MusicSongScreenEvent()
    data class OnMusicComponentClick(val musicListType: MusicListType): MusicSongScreenEvent()
    data class OnRefresh(val context: Context, val musicListType: MusicListType): MusicSongScreenEvent()
}