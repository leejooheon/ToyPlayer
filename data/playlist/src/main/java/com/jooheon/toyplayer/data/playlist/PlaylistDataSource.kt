package com.jooheon.toyplayer.data.playlist

import com.jooheon.toyplayer.data.playlist.dao.PlaylistDao
import com.jooheon.toyplayer.data.playlist.dao.data.PlaylistEntity.Companion.toPlaylistEntity
import com.jooheon.toyplayer.domain.model.music.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistDataSource @Inject constructor(
    private val dao: PlaylistDao,
) {
    fun getAllPlaylist(): Flow<List<Playlist>> {
        return dao.getAllPlaylist()
            .map { playlists ->
                playlists.map { it.toPlaylist() }
            }
    }

    fun flowPlaylist(id: Int): Flow<Playlist?> {
        return dao.get(id).map { it?.toPlaylist() }
    }

    suspend fun updatePlaylists(playlist: Playlist) {
        dao.update(playlist.toPlaylistEntity())
    }

    suspend fun insertPlaylists(playlist: Playlist) {
        dao.insert(playlist.toPlaylistEntity())
    }

    suspend fun deletePlaylists(playlist: Playlist) {
        dao.delete(playlist.toPlaylistEntity())
    }
}