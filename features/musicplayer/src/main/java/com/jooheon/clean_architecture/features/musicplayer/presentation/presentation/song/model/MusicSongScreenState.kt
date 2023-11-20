package com.jooheon.clean_architecture.features.musicplayer.presentation.presentation.song.model

import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicservice.data.MusicState

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