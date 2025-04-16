package com.jooheon.toyplayer.features.playlist.details.model

import android.content.Context
import android.net.Uri
import com.jooheon.toyplayer.domain.model.music.Song

sealed interface PlaylistDetailEvent {
    data class OnPlay(val index: Int): PlaylistDetailEvent
    data class OnPlayAll(val shuffle: Boolean): PlaylistDetailEvent
    data class OnDelete(val song: Song): PlaylistDetailEvent
    data class OnPermissionRequest(val context: Context): PlaylistDetailEvent
    data class OnThumbnailImageSelected(val context: Context, val uri: Uri): PlaylistDetailEvent
}