package com.jooheon.toyplayer.features.album.more.model

import com.jooheon.toyplayer.domain.model.music.Album

data class AlbumMoreUiState(
    val albums: List<Album>,
) {
    companion object {
        val default = AlbumMoreUiState(
            albums = listOf(Album.default)
        )
    }
}