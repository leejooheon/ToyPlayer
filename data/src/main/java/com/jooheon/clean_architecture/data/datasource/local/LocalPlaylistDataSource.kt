package com.jooheon.clean_architecture.data.datasource.local

import com.jooheon.clean_architecture.data.dao.playlist.PlaylistDao
import com.jooheon.clean_architecture.data.dao.playlist.data.PlaylistMapper
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import javax.inject.Inject

class LocalPlaylistDataSource @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistMapper: PlaylistMapper,
): BaseLocalDataSource {

    fun getAllPlaylist(): List<Playlist> {
        return playlistDao.getAllPlaylist().map {
            playlistMapper.map(it)
        }
    }

    fun getPlaylist(id: Int): Playlist? {
        val entity = playlistDao.get(id) ?: return null
        return playlistMapper.map(entity)
    }

    suspend fun updatePlaylists(vararg playlist: Playlist) {
        val entity = playlist.map {
            playlistMapper.mapInverse(it)
        }.toTypedArray()

        playlistDao.update(*entity)
    }

    suspend fun insertPlaylists(vararg playlist: Playlist) {
        val entity = playlist.map {
            playlistMapper.mapInverse(it)
        }.toTypedArray()

        playlistDao.insert(*entity)
    }

    suspend fun deletePlaylists(vararg playlist: Playlist) {
        val entity = playlist.map {
            playlistMapper.mapInverse(it)
        }.toTypedArray()

        playlistDao.delete(*entity)
    }
}