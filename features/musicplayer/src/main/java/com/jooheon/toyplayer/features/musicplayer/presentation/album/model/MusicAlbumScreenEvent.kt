package com.jooheon.toyplayer.features.musicplayer.presentation.album.model

import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.features.musicplayer.presentation.album.MusicAlbumScreenViewModel.AlbumSortType

sealed class MusicAlbumScreenEvent {
    data class OnAlbumItemClick(val album: com.jooheon.toyplayer.domain.model.music.Album): MusicAlbumScreenEvent()
    data class OnSortTypeChanged(val type: AlbumSortType): MusicAlbumScreenEvent()
}