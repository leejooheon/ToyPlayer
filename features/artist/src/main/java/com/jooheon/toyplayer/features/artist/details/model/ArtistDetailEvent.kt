package com.jooheon.toyplayer.features.artist.details.model

sealed interface ArtistDetailEvent {
    data object Placeholder: ArtistDetailEvent
}