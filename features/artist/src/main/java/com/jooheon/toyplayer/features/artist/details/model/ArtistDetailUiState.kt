package com.jooheon.toyplayer.features.artist.details.model

import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

data class ArtistDetailUiState(
    val artist: Artist,
    val playlists: List<Playlist>,
) {
    companion object {
        val default = ArtistDetailUiState(
            artist = Artist.default,
            playlists = listOf(Playlist.default)
        )
    }

    data class DialogState(
        val type: Type,
        val song: Song,
    ) {
        enum class Type {
            None, SongInfo, SelectPlaylist, NewPlaylist
        }
        companion object {
            val default = DialogState(
                type = Type.None,
                song = Song.default,
            )
        }
    }
}