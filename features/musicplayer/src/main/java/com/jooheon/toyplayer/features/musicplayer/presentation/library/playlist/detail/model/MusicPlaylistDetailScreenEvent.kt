package com.jooheon.toyplayer.features.musicplayer.presentation.library.playlist.detail.model

import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicPlaylistDetailScreenEvent {
    data object OnBackClick: MusicPlaylistDetailScreenEvent()
}