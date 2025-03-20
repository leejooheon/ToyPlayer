package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.playlist.PlaylistDataSource
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaylistError
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
): PlaylistRepository {
    override suspend fun getAllPlaylist(): Result<List<Playlist>, PlaylistError> {
        val list = playlistDataSource.getAllPlaylist()
        return Result.Success(list)
    }

    override suspend fun updatePlaylists(vararg playlist: Playlist) {
        playlistDataSource.updatePlaylists(*playlist)
    }

    override suspend fun insertPlaylists(vararg playlist: Playlist) {
        playlistDataSource.insertPlaylists(*playlist)
    }

    override suspend fun deletePlaylists(vararg playlist: Playlist) {
        playlistDataSource.deletePlaylists(*playlist)
    }

    override suspend fun getPlaylist(id: Int): Result<Playlist, PlaylistError> {
        val playlistOrNull = playlistDataSource.getPlaylist(id)
        return if(playlistOrNull == null) {
            Result.Error(PlaylistError.NotFound(id))
        } else {
            Result.Success(playlistOrNull)
        }
    }
}