package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Result<List<Playlist>, PlaybackDataError>
    suspend fun getPlaylist(id: Int): Result<Playlist, PlaybackDataError>

    fun flowAllPlaylists(): Flow<List<Playlist>>
    fun flowPlaylist(id: Int): Flow<Playlist?>

    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
}