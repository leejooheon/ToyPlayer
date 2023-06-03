package com.jooheon.clean_architecture.domain.entity.music

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val audioId: Long,
    val displayName: String,
    val title: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val albumId: String,
    val duration: Long,
    val path: String,
    val imageUrl: String,
    var isFavorite: Boolean = false,
): java.io.Serializable {

    companion object {
        val default = Song(
            audioId = -1L,
            displayName = "-",
            title = "-",
            artist = "<unknown>",
            artistId = "",
            album = "-",
            albumId = "-",
            duration = 1L,
            path = "-",
            imageUrl = "",
            isFavorite = false
        )
    }
}