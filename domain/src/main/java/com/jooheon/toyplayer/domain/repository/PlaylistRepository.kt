package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.common.Result
import com.jooheon.toyplayer.domain.common.errors.RootError
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Result<List<Playlist>, RootError>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)

    suspend fun getPlayingQueue(): Result<List<Song>, RootError>
    suspend fun updatePlayingQueue(songs: List<Song>)
    suspend fun clear()
}