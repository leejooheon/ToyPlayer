package com.jooheon.toyplayer.domain.repository.library

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Playlist

interface PlaylistRepository {
    suspend fun getAllPlaylist(): Resource<List<Playlist>>
    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
}