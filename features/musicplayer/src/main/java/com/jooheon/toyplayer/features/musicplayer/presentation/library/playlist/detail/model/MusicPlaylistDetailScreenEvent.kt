package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.model

sealed class MusicPlaylistDetailScreenEvent {
    data object OnBackClick: MusicPlaylistDetailScreenEvent()
}