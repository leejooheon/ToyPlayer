package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.artist.model

import com.jooheon.clean_architecture.domain.entity.music.Artist

sealed class MusicArtistScreenEvent {
    data class OnArtistItemClick(val artist: Artist): MusicArtistScreenEvent()
    data object OnSortByArtistName: MusicArtistScreenEvent()
    data object OnSortByNumberOfSong: MusicArtistScreenEvent()
    data object OnSortByNumberOfAlbum: MusicArtistScreenEvent()

    companion object {
        fun indexToEvent(index: Int): MusicArtistScreenEvent {
            return when(index) {
                0 -> OnSortByArtistName
                1 -> OnSortByNumberOfSong
                2 -> OnSortByNumberOfAlbum
                else -> OnSortByArtistName
            }
        }
    }
}