package com.jooheon.toyplayer.features.playlist.details.model

sealed interface PlaylistDetailEvent {
    data object Placeholder: PlaylistDetailEvent
}