package com.jooheon.toyplayer.data.playlist

import com.jooheon.toyplayer.data.playlist.dao.PlaylistDao
import com.jooheon.toyplayer.data.playlist.dao.data.PlaylistMapper
import com.jooheon.toyplayer.domain.model.music.Playlist
import javax.inject.Inject

class PlaylistDataSource @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistMapper: PlaylistMapper,
) {
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