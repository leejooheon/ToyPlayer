package com.jooheon.toyplayer.features.musicplayer.presentation.artist.model

import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.MusicArtistScreenViewModel.ArtistSortType

sealed class MusicArtistScreenEvent {
    data class OnArtistItemClick(val artist: com.jooheon.toyplayer.domain.model.music.Artist): MusicArtistScreenEvent()
    data class OnSortTypeChanged(val type: ArtistSortType): MusicArtistScreenEvent()
}