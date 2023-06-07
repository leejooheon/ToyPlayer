package com.jooheon.clean_architecture.domain.entity.music

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: Int,
    val name: String,
    var thumbnailUrl: String,
    var songs: List<Song> = emptyList(),
): java.io.Serializable {
    companion object {
        val default = Playlist(
            id = -1,
            name = "-",
            thumbnailUrl = "",
            songs = emptyList()
        )
    }
}