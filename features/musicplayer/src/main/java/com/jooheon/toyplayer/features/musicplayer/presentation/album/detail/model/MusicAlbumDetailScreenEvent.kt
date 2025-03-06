package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model

sealed class MusicAlbumDetailScreenEvent {
    data object OnBackClick: MusicAlbumDetailScreenEvent()
}