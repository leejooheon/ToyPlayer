package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.playlist.PlaylistDataSource
import com.jooheon.toyplayer.domain.common.FailureStatus
import com.jooheon.toyplayer.domain.common.Result
import com.jooheon.toyplayer.domain.common.errors.RootError
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.PlaylistRepository
import kotlinx.coroutines.runBlocking

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
): PlaylistRepository {
    init {
        runBlocking {
            maybeMakePlayingQueueDb()
        }
    }

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

    override suspend fun getPlayingQueue(): Result<List<Song>, RootError> {
        val playingQueue = playlistDataSource.getPlaylist(Playlist.PlayingQueuePlaylistId)
        return Result.Success(playingQueue?.songs.defaultEmpty())
    }

    override suspend fun updatePlayingQueue(songs: List<Song>) {
        playlistDataSource.updatePlaylists(
            Playlist.playingQueuePlaylist.copy(
                songs = songs
            )
        )
    }

    override suspend fun clear() {
        playlistDataSource.updatePlaylists(
            Playlist.playingQueuePlaylist.copy(
                songs = emptyList()
            )
        )
    }

    private suspend fun maybeMakePlayingQueueDb() {
        val playlist = playlistDataSource.getPlaylist(Playlist.PlayingQueuePlaylistId)
        if (playlist == null) {
            playlistDataSource.insertPlaylists(Playlist.playingQueuePlaylist)
        }
    }
}