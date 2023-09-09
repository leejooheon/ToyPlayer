package com.jooheon.clean_architecture.domain.repository.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Resource<List<Playlist>>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
}