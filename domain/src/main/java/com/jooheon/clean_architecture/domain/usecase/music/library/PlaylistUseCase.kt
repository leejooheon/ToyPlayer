package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistUseCase {
    fun allPlaylist(): Flow<List<Playlist>>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
    suspend fun update()
}