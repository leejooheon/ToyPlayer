package com.jooheon.clean_architecture.data.repository.library

import com.jooheon.clean_architecture.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.library.PlayingQueueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlayingQueueRepositoryImpl(
    applicationScope: CoroutineScope,
    private val localPlaylistDataSource: LocalPlaylistDataSource
): PlayingQueueRepository {
    init {
        applicationScope.launch {
            maybeMakePlayingQueueDb()
        }
    }

    override suspend fun getPlayingQueue(): Resource<List<Song>> {
        val playingQueue = localPlaylistDataSource.getPlaylist(PlayingQueuePlaylistId)

        val resource = if(playingQueue != null) {
            Resource.Success(playingQueue.songs)
        } else {
            Resource.Failure(
                failureStatus = FailureStatus.JSON_PARSE
            )
        }
        return resource
    }

    override suspend fun updatePlayingQueue(songs: List<Song>) {
        localPlaylistDataSource.updatePlaylists(
            playingQueuePlaylist.copy(
                songs = songs
            )
        )
    }

    override suspend fun clear() {
        localPlaylistDataSource.updatePlaylists(
            playingQueuePlaylist.copy(
                songs = emptyList()
            )
        )
    }

    private suspend fun maybeMakePlayingQueueDb() {
        val playlist = localPlaylistDataSource.getPlaylist(PlayingQueuePlaylistId)
        if(playlist == null) {
            localPlaylistDataSource.insertPlaylists(playingQueuePlaylist)
        }
    }

    companion object {
        const val PlayingQueuePlaylistId = -1000

        val playingQueuePlaylist = Playlist(
            id = PlayingQueuePlaylistId,
            name = "",
            thumbnailUrl = ""
        )
    }
}