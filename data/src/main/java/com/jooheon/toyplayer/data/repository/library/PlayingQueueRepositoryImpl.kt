package com.jooheon.toyplayer.data.repository.library

import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.data.datasource.local.LocalPlaylistDataSource
import com.jooheon.toyplayer.domain.common.FailureStatus
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.RepeatMode
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.library.PlayingQueueRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PlayingQueueRepositoryImpl(
    applicationScope: CoroutineScope,
    private val localPlaylistDataSource: LocalPlaylistDataSource,
    private val appPreferences: AppPreferences
): PlayingQueueRepository {
    init {
        applicationScope.launch {
            maybeMakePlayingQueueDb()
        }
    }

    override suspend fun setRepeatMode(repeatMode: Int) {
        appPreferences.repeatMode = RepeatMode.getByValue(repeatMode)
    }
    override suspend fun setShuffleMode(shuffleEnabled: Boolean) {
        appPreferences.shuffleMode = ShuffleMode.getByValue(shuffleEnabled)
    }
    override suspend fun getRepeatMode() = appPreferences.repeatMode
    override suspend fun getShuffleMode() = appPreferences.shuffleMode
    override suspend fun getPlayingQueueKey(): Long {
        return appPreferences.lastPlayingQueuePosition
    }

    override suspend fun setPlayingQueueKey(key: Long) {
        appPreferences.lastPlayingQueuePosition = key
    }

    override suspend fun getPlayingQueue(): Resource<List<Song>> {
        val playingQueue = localPlaylistDataSource.getPlaylist(Playlist.PlayingQueuePlaylistId)

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
            Playlist.playingQueuePlaylist.copy(
                songs = songs
            )
        )
    }

    override suspend fun clear() {
        localPlaylistDataSource.updatePlaylists(
            Playlist.playingQueuePlaylist.copy(
                songs = emptyList()
            )
        )
    }

    private suspend fun maybeMakePlayingQueueDb() {
        val playlist = localPlaylistDataSource.getPlaylist(Playlist.PlayingQueuePlaylistId)
        if(playlist == null) {
            localPlaylistDataSource.insertPlaylists(Playlist.playingQueuePlaylist)
        }
    }
}