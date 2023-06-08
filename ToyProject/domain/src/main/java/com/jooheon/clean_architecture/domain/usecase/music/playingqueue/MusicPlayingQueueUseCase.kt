package com.jooheon.clean_architecture.domain.usecase.music.playingqueue

import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.playlist.MusicPlaylistUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayingQueueUseCase @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val musicPlaylistUseCase: MusicPlaylistUseCase,
) {
    private val _playingQueueState = MutableStateFlow<Resource<Playlist>>(Resource.Default)
    val playingQueueState = _playingQueueState.asStateFlow()

    init {
        loadPlayingQueue()
    }

    fun addToPlayingQueue(vararg song: Song) = applicationScope.launch {
        val state = playingQueueState.value

        if(state !is Resource.Success) return@launch

        val playlist = state.value

        val newSongs = playlist.songs.toMutableList().apply {
            addAll(song)
        }
        val newPlaylist = playlist.copy(
            songs = newSongs
        )

        musicPlaylistUseCase.updatePlaylists(newPlaylist)
        loadPlayingQueue()
    }

    fun deletePlayingQueue(vararg song: Song) = applicationScope.launch {
        val state = playingQueueState.value

        if(state !is Resource.Success) return@launch

        val playlist = state.value

        val newSongs = playlist.songs.toMutableList().apply {
            removeAll(song.toSet())
        }
        val newPlaylist = playlist.copy(
            songs = newSongs
        )

        musicPlaylistUseCase.updatePlaylists(newPlaylist)
        loadPlayingQueue()
    }

    fun clear() = applicationScope.launch {
        val state = playingQueueState.value
        if(state !is Resource.Success) return@launch
        val playlist = state.value

        musicPlaylistUseCase.deletePlaylists(playlist)
    }

    private fun loadPlayingQueue() {
        val state = playingQueueState.value
        if(state == Resource.Loading) return

        _playingQueueState.tryEmit(Resource.Loading)

        musicPlaylistUseCase
            .getPlaylist(PlayingQueuePlaylistId)
            .onEach {
                _playingQueueState.tryEmit(it)

                if(isRequireInsert(it)) {
                    musicPlaylistUseCase.insertPlaylists(playingQueuePlaylist)
                    loadPlayingQueue()
                    return@onEach
                }
            }.launchIn(applicationScope)
    }

    private fun isRequireInsert(resource: Resource<Playlist>): Boolean {
        val failureResource = resource as? Resource.Failure ?: return false
        val state = failureResource.failureStatus

        return state == FailureStatus.EMPTY
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