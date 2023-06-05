package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
interface PlaylistRepository: BaseRepository {
    suspend fun getAllPlaylist(): Resource<List<Playlist>>
    suspend fun getPlaylist(id: Int): Resource<Playlist>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
}