package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val localPlaylistDataSource: LocalPlaylistDataSource,
): PlaylistRepository {
    override suspend fun getAllPlaylist(): Resource<List<Playlist>> {
        val list = localPlaylistDataSource.getAllPlaylist()
        return Resource.Success(list)
    }

    override suspend fun getPlaylist(id: Int): Resource<Playlist> {
        val playlist = localPlaylistDataSource.getPlaylist(id)

        val resource = if(playlist == null) {
            Resource.Failure(failureStatus = FailureStatus.EMPTY)
        } else {
            Resource.Success(playlist)
        }

        return resource
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