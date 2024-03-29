package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model

import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song

data class MusicSongScreenState(
    val songList: List<Song>,
    val playlists: List<Playlist>,
    val musicListType: MusicListType,
) {
    companion object {
        val default = MusicSongScreenState(
            songList = Song.defaultList,
            playlists = listOf(Playlist.default),
            musicListType = MusicListType.All,
        )
    }
}