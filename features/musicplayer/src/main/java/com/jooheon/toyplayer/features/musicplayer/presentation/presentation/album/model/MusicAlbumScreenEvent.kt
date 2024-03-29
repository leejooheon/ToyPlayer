package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.model

import com.jooheon.toyplayer.domain.entity.music.Album

sealed class MusicAlbumScreenEvent {
    data class OnAlbumItemClick(val album: Album): MusicAlbumScreenEvent()
    data object OnSortByAlbumName: MusicAlbumScreenEvent()
    data object OnSortByArtistName: MusicAlbumScreenEvent()

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