package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaylistError
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Result<List<Playlist>, PlaylistError>
    suspend fun getPlaylist(id: Int): Result<Playlist, PlaylistError>

    fun flowAllPlaylists(): Flow<List<Playlist>>
    fun flowPlaylist(id: Int): Flow<Playlist?>

    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
}