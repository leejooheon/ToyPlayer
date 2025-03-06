package com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.model

import com.jooheon.toyplayer.domain.model.music.MusicListType
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song


data class MusicListDetailScreenState(
    val songList: List<Song>,
    val playlists: List<Playlist>,
    val musicListType: MusicListType,
) {
    companion object {
        val default = MusicListDetailScreenState(
            songList = emptyList(),
            playlists = listOf(Playlist.default),
            musicListType = MusicListType.All,
        )
    }
}