package com.jooheon.toyplayer.features.musicplayer.presentation.song.model

import com.jooheon.toyplayer.domain.model.music.MusicListType
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

data class MusicSongScreenState(
    val songList: List<Song>,
    val playlists: List<Playlist>,
    val musicListType: MusicListType,
) {
    companion object {
        val default = MusicSongScreenState(
            songList = emptyList(),
            playlists = listOf(Playlist.default),
            musicListType = MusicListType.All,
        )
    }
}