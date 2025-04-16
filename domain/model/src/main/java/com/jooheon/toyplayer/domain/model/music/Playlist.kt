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

        private enum class DefaultPlaylistId(val id: Int) {
            PlayingQueue(0),
            Radio(1),
            Stream(2),
            Local(3),
            Favorite(4);
        }

        val defaultPlaylists = DefaultPlaylistId.entries.map { MediaId.Playlist(it.id) }

        val PlayingQueue get() = defaultPlaylists[DefaultPlaylistId.PlayingQueue.ordinal]
        val Radio get() = defaultPlaylists[DefaultPlaylistId.Radio.ordinal]
        val Stream get() = defaultPlaylists[DefaultPlaylistId.Stream.ordinal]
        val Local get() = defaultPlaylists[DefaultPlaylistId.Local.ordinal]
        val Favorite get() = defaultPlaylists[DefaultPlaylistId.Favorite.ordinal]
    }
}