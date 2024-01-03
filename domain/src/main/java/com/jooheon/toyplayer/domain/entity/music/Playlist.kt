package com.jooheon.toyplayer.domain.entity.music

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
            songs = Song.defaultList,
        )

        const val PlayingQueuePlaylistId = -1000
        val playingQueuePlaylist = Playlist(
            id = PlayingQueuePlaylistId,
            name = "PlayingQueue",
            thumbnailUrl = "",
            songs = emptyList()
        )
    }
}