package com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model

import androidx.navigation.NavController
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.features.common.compose.ScreenNavigation

sealed class MusicArtistScreenEvent {
    data class OnArtistItemClick(val artist: Artist): MusicArtistScreenEvent()
}