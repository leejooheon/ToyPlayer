package com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.model

import android.content.Context
import com.jooheon.toyplayer.domain.model.music.MusicListType

sealed class MusicListDetailScreenEvent {
    data class OnRefresh(val context: Context, val musicListType: MusicListType): MusicListDetailScreenEvent()
}