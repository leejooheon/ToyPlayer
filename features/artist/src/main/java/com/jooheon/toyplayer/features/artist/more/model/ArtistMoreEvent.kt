package com.jooheon.toyplayer.features.artist.more.model

sealed interface ArtistMoreEvent {
    data class OnArtistClick(val id: String): ArtistMoreEvent
}