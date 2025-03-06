package com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.model

import com.jooheon.toyplayer.domain.model.music.Playlist


data class MusicArtistDetailScreenState(
    val artist: com.jooheon.toyplayer.domain.model.music.Artist,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = MusicArtistDetailScreenState(
            artist = com.jooheon.toyplayer.domain.model.music.Artist.default.copy(
                name = "Artist name",
            ),
            playlists = listOf(Playlist.default)
        )
    }
}