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
        val preview = Playlist(
            id = -1,
            name = "preview-playlist-name",
            thumbnailUrl = "",
            songs = listOf(Song.preview, Song.preview, Song.preview, Song.preview),
        )

        val defaultPlaylistIds = listOf(
            MediaId.PlayingQueue,
            MediaId.RadioSongs,
            MediaId.StreamSongs,
            MediaId.LocalSongs,
        )
    }
}