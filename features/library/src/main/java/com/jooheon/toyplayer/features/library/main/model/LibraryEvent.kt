package com.jooheon.toyplayer.features.library.main.model

sealed interface LibraryEvent {
    data class OnPlaylistClick(val id: Int): LibraryEvent
    data object OnPlaylistMainClick: LibraryEvent

    data class OnArtistClick(val id: String): LibraryEvent
    data object OnArtistMoreClick: LibraryEvent
}