package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.model

import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.MusicArtistScreenViewModel.ArtistSortType

sealed class MusicArtistScreenEvent {
    data class OnArtistItemClick(val artist: Artist): MusicArtistScreenEvent()
    data class OnSortTypeChanged(val type: ArtistSortType): MusicArtistScreenEvent()
}