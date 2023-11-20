package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.album.model

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Album

data class MusicAlbumScreenState(
    val albums: List<Album>,
) {
    companion object {
        val default = MusicAlbumScreenState(
            albums = Album.defaultList
        )
    }
}