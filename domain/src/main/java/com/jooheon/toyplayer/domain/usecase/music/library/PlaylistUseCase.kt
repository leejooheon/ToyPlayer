package com.jooheon.toyplayer.domain.usecase.music.library

import com.jooheon.toyplayer.domain.entity.music.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistUseCase {
    fun allPlaylist(): Flow<List<Playlist>>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
    suspend fun update()
}