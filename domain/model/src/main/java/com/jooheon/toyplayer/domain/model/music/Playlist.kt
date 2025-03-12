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
            MediaId.AssetSongs, // (maybe replace to radio)
        )

        val defaultPlaylists = defaultPlaylistIds.map {
            val id = it.hashCode()
            Playlist(
                id = id,
                name = getDefaultPlaylistName(id),
                thumbnailUrl = "",
                songs = emptyList()
            )
        }

        fun getDefaultPlaylist(mediaId: MediaId): Playlist {
            if(mediaId !in defaultPlaylistIds) throw IllegalArgumentException("$mediaId is not default playlist.")
            val playlist = defaultPlaylists.first { it.id == mediaId.hashCode() }
            return playlist
        }

        fun getDefaultPlaylistName(mediaIdInt: Int): String { // FIXME string resource 사용하자
            return when(mediaIdInt) {
                MediaId.PlayingQueue.hashCode() -> "Playing Queue"
                MediaId.RadioSongs.hashCode() -> "Radio Songs"
                MediaId.LocalSongs.hashCode() -> "Local Songs"
                MediaId.StreamSongs.hashCode() -> "Stream Songs"
                MediaId.AssetSongs.hashCode() -> "Asset Songs"
                else -> throw IllegalArgumentException("$mediaIdInt is not default playlist.")
            }
        }
    }
}