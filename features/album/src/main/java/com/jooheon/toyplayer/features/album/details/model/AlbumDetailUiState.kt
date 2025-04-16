package com.jooheon.toyplayer.features.album.details.model

import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Playlist

data class AlbumDetailUiState(
    val album: Album,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = AlbumDetailUiState(
            album = Album.default,
            playlists = emptyList(),
        )
        val preview = AlbumDetailUiState(
            album = Album.default,
            playlists = listOf(Playlist.preview),
        )
    }
}