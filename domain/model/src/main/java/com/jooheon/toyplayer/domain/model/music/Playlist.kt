package com.jooheon.toyplayer.domain.model.music

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: Int,
    val name: String,
    var thumbnailUrl: String,
    var songs: List<Song> = emptyList(),
) {
    companion object {
        val default = Playlist(
            id = -1,
            name = "-",
            thumbnailUrl = "",
            songs = emptyList(),
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