package com.jooheon.toyplayer.data.playlist

import com.jooheon.toyplayer.data.playlist.dao.PlaylistDao
import com.jooheon.toyplayer.data.playlist.dao.data.PlaylistEntity.Companion.toPlaylistEntity
import com.jooheon.toyplayer.domain.model.music.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistDataSource @Inject constructor(
    private val playlistDao: PlaylistDao,
) {
    fun getAllPlaylist(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylist()
            .map { playlists ->
                playlists.map { it.toPlaylist() }
            }
    }

    fun flowPlaylist(id: Int): Flow<Playlist?> {
        return playlistDao.get(id).map { it?.toPlaylist() }
    }

    suspend fun updatePlaylists(playlist: Playlist) {
        playlistDao.update(playlist.toPlaylistEntity())
    }

    suspend fun insertPlaylists(playlist: Playlist) {
        playlistDao.insert(playlist.toPlaylistEntity())
    }

    suspend fun deletePlaylists(playlist: Playlist) {
        playlistDao.delete(playlist.toPlaylistEntity())
    }
}