package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.playlist.PlaylistDataSource
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
): PlaylistRepository {
    override suspend fun getAllPlaylist(): Result<List<Playlist>, PlaybackDataError> {
        val list = playlistDataSource.getAllPlaylist().firstOrNull().defaultEmpty()
        return Result.Success(list)
    }

    override suspend fun getPlaylist(id: Int): Result<Playlist, PlaybackDataError> {
        val playlistOrNull = playlistDataSource.flowPlaylist(id).firstOrNull()
        return if(playlistOrNull == null) {
            Result.Error(PlaybackDataError.PlaylistNotFound(id))
        } else {
            Result.Success(playlistOrNull)
        }
    }

    override fun flowAllPlaylists(): Flow<List<Playlist>> {
        return playlistDataSource.getAllPlaylist()
    }

    override fun flowPlaylist(id: Int): Flow<Playlist?> {
        return playlistDataSource.flowPlaylist(id)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDataSource.updatePlaylists(playlist)
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDataSource.insertPlaylists(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDataSource.deletePlaylists(playlist)
    }
}