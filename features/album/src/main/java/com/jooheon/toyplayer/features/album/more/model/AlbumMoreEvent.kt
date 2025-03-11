package com.jooheon.toyplayer.features.album.more.model

sealed interface AlbumMoreEvent {
    data class OnAlbumClick(val albumId: String): AlbumMoreEvent
}