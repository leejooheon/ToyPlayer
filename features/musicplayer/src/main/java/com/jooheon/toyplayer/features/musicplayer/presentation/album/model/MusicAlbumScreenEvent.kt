package com.jooheon.toyplayer.features.musicplayer.presentation.album.model

import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.features.musicplayer.presentation.album.MusicAlbumScreenViewModel.AlbumSortType

sealed class MusicAlbumScreenEvent {
    data class OnAlbumItemClick(val album: Album): MusicAlbumScreenEvent()
    data class OnSortTypeChanged(val type: AlbumSortType): MusicAlbumScreenEvent()
}