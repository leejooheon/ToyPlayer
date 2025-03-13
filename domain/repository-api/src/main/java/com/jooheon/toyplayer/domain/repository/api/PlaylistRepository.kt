package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.common.Result

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Result<List<Playlist>, RootError>
    suspend fun getPlaylist(id: Int): Result<Playlist, RootError>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
}