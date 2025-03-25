package com.jooheon.toyplayer.features.playlist.details.model

import android.content.Context
import com.jooheon.toyplayer.domain.model.music.Song

sealed interface PlaylistDetailEvent {
    data class OnPlayAllClick(val shuffle: Boolean): PlaylistDetailEvent
    data class OnDelete(val song: Song): PlaylistDetailEvent
    data class OnPermissionRequest(val context: Context): PlaylistDetailEvent
}