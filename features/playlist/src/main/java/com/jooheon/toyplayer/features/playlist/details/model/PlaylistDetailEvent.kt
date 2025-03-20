package com.jooheon.toyplayer.features.playlist.details.model

import com.jooheon.toyplayer.domain.model.music.Song

sealed interface PlaylistDetailEvent {
    data class OnPlayAllClick(val shuffle: Boolean): PlaylistDetailEvent
    data class OnDelete(val song: Song): PlaylistDetailEvent
}