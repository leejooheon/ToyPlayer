package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.playlist.PlaylistDataSource
import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.ResourceError
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Song

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
): PlaylistRepository {
    override suspend fun getAllPlaylist(): Result<List<Playlist>, RootError> {
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

    override suspend fun getPlaylist(id: Int): Result<Playlist, RootError> {
        val playlistOrNull = playlistDataSource.getPlaylist(id)
        return if(playlistOrNull == null) {
            Result.Error(ResourceError.Unknown("$id playlist not found"))
        } else {
            Result.Success(playlistOrNull)
        }
    }

    override suspend fun getPlayingQueue(): Result<List<Song>, RootError> {
        val playingQueue = playlistDataSource.getPlaylist(MediaId.PlayingQueue.hashCode())
        return Result.Success(playingQueue?.songs.defaultEmpty())
    }

    override suspend fun updatePlayingQueue(songs: List<Song>) {
        playlistDataSource.updatePlaylists(
            Playlist.getDefaultPlaylist(MediaId.PlayingQueue).copy(
                songs = songs
            )
        )
    }

    override suspend fun clear() {
        playlistDataSource.updatePlaylists(
            Playlist.getDefaultPlaylist(MediaId.PlayingQueue).copy(
                songs = emptyList()
            )
        )
    }
}