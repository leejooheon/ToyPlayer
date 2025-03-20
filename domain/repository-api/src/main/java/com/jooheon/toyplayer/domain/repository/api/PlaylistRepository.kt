package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaylistError

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Result<List<Playlist>, PlaylistError>
    suspend fun getPlaylist(id: Int): Result<Playlist, PlaylistError>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
}