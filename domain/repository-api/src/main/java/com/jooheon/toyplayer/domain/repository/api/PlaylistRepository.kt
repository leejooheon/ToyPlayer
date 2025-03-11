package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.music.Song

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Result<List<Playlist>, RootError>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
    suspend fun getPlaylist(id: Int): Result<Playlist, RootError>
    suspend fun getPlayingQueue(): Result<List<Song>, RootError>
    suspend fun updatePlayingQueue(songs: List<Song>)
    suspend fun clear()
}