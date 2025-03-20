package com.jooheon.toyplayer.features.artist.details.model

import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.Playlist

data class ArtistDetailUiState(
    val artist: Artist,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = ArtistDetailUiState(
            artist = Artist.default,
            playlists = listOf(Playlist.default)
        )
    }
}