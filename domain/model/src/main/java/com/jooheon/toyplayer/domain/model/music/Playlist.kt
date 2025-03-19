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
            name = "",
            thumbnailUrl = "",
            songs = emptyList(),
        )
        val preview = Playlist(
            id = -1,
            name = "preview-playlist-name",
            thumbnailUrl = "",
            songs = listOf(Song.preview, Song.preview, Song.preview, Song.preview),
        )

        val PlayingQueuePlaylistId = Pair(0, MediaId.PlayingQueue)
        val RadioPlaylistId = Pair(1, MediaId.RadioSongs)
        val StreamPlaylistId = Pair(2, MediaId.StreamSongs)
        val LocalPlaylistId = Pair(3, MediaId.LocalSongs)

        val defaultPlaylistIds = listOf(
            PlayingQueuePlaylistId,
            RadioPlaylistId,
            StreamPlaylistId,
            LocalPlaylistId,
        )
    }
}