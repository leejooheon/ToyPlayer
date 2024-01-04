package com.jooheon.toyplayer.data.repository.library

import com.jooheon.toyplayer.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.repository.library.PlaylistRepository

class PlaylistRepositoryImpl(
    private val localPlaylistDataSource: LocalPlaylistDataSource,
): PlaylistRepository {

    override suspend fun getAllPlaylist(): Resource<List<Playlist>> {
        val list = localPlaylistDataSource.getAllPlaylist()
        return Resource.Success(list)
    }

    override suspend fun updatePlaylists(vararg playlist: Playlist) {
        localPlaylistDataSource.updatePlaylists(*playlist)
    }

    override suspend fun insertPlaylists(vararg playlist: Playlist) {
        localPlaylistDataSource.insertPlaylists(*playlist)
    }

    override suspend fun deletePlaylists(vararg playlist: Playlist) {
        localPlaylistDataSource.deletePlaylists(*playlist)
    }
}