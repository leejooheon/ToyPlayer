package com.jooheon.toyplayer.features.musicplayer.presentation.album.model

import com.jooheon.toyplayer.domain.model.music.Album

data class MusicAlbumScreenState(
    val albums: List<Album>,
) {
    companion object {
        val default = MusicAlbumScreenState(
            albums = listOf(Album.default)
        )
    }
}