package com.jooheon.toyplayer.features.playlist.details.model

sealed interface PlaylistDetailEvent {
    data class OnPlayAllClick(val shuffle: Boolean): PlaylistDetailEvent
}