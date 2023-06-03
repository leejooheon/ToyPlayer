package com.jooheon.clean_architecture.features.musicplayer.presentation.album.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicAlbumDetailScreenEvent {
    object OnBackClick: MusicAlbumDetailScreenEvent()
    data class OnSongClick(val song: Song): MusicAlbumDetailScreenEvent()
}