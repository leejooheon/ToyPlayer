package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model

import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicAlbumDetailScreenEvent {
    data object OnBackClick: MusicAlbumDetailScreenEvent()
}