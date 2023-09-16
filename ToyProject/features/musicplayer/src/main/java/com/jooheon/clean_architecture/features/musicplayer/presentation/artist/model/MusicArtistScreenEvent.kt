package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model

import com.jooheon.clean_architecture.domain.entity.music.Artist

sealed class MusicArtistScreenEvent {
    data class OnArtistItemClick(val artist: Artist): MusicArtistScreenEvent()
    object OnSortByArtistName: MusicArtistScreenEvent()
    object OnSortByNumberOfSong: MusicArtistScreenEvent()
    object OnSortByNumberOfAlbum: MusicArtistScreenEvent()

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