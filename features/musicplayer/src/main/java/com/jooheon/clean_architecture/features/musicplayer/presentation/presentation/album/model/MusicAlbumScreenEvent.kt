package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.album.model

import com.jooheon.clean_architecture.domain.entity.music.Album

sealed class MusicAlbumScreenEvent {
    data class OnAlbumItemClick(val album: Album): MusicAlbumScreenEvent()
    object OnSortByAlbumName: MusicAlbumScreenEvent()
    object OnSortByArtistName: MusicAlbumScreenEvent()

    companion object {

        fun indexToEvent(index: Int): MusicAlbumScreenEvent {
            return when(index) {
                0 -> OnSortByAlbumName
                1 -> OnSortByArtistName
                else -> OnSortByAlbumName
            }
        }
    }
}