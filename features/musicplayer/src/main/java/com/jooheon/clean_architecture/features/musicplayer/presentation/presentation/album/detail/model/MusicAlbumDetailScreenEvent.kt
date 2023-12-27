package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.album.detail.model

import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Song

sealed class MusicAlbumDetailScreenEvent {
    data object OnBackClick: MusicAlbumDetailScreenEvent()
    data class OnSongClick(val song: Song): MusicAlbumDetailScreenEvent()
    data class OnActionPlayAll(
        val album: Album,
        val shuffle: Boolean
    ): MusicAlbumDetailScreenEvent()
}