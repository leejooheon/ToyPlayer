package com.jooheon.clean_architecture.features.musicplayer.presentation.album.model

import com.jooheon.clean_architecture.domain.entity.music.Album

sealed class MusicAlbumScreenEvent {
    data class OnAlbumItemClick(val album: Album): MusicAlbumScreenEvent()
    object OnSortByAlbumName: MusicAlbumScreenEvent()
    object OnSortByArtistName: MusicAlbumScreenEvent()

}