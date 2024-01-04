package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.detail.model

import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Song

sealed class MusicAlbumDetailScreenEvent {
    data object OnBackClick: MusicAlbumDetailScreenEvent()
    data class OnSongClick(val song: Song): MusicAlbumDetailScreenEvent()
    data class OnPlayAllClick(
        val album: Album,
        val shuffle: Boolean
    ): MusicAlbumDetailScreenEvent()
}