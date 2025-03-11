package com.jooheon.toyplayer.features.album.details.model

sealed interface AlbumDetailEvent {
    data object Placeholder: AlbumDetailEvent
}