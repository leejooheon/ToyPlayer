package com.jooheon.clean_architecture.features.musicservice.usecase.manager

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.PlaylistType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.MusicPlayListUsecase
import com.jooheon.clean_architecture.features.common.utils.MusicUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MusicPlayListManager (
    private val applicationScope: CoroutineScope,
    private val musicPlayListUsecase: MusicPlayListUsecase,
) {
    var playlist: MutableList<Song> = mutableListOf()
        private set
    var latestFailureResource: Resource.Failure? = null

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state = State.CREATED
        set(value) {
            if (value == State.INITIALIZED || value == State.ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach {
                        it(value == State.INITIALIZED)
                    }

                    onReadyListeners.clear()
                }
            } else field = value
        }

    fun loadPlaylist(playlistType: PlaylistType): MusicPlayListManager {
        if (state == State.INITIALIZING) { return this }

        state = State.INITIALIZING

        applicationScope.launch {
            val resources = when(playlistType) {
                PlaylistType.Local -> listOf(getLocalPlaylist())
                PlaylistType.Streaming -> listOf(getStreamingPlaylist())
                PlaylistType.All -> listOf(getStreamingPlaylist(), getLocalPlaylist())
            }

            resources.firstOrNull { it  is Resource.Loading }?.let {
                state = State.INITIALIZING
                return@launch
            }

            resources.firstOrNull { it is Resource.Failure }?.let {
                playlist = Collections.emptyList()
                latestFailureResource = it as Resource.Failure
                state = State.ERROR
                return@launch
            }

            resources.firstOrNull { it is Resource.Default }?.let {
                state = State.ERROR
                return@launch
            }

            val newPlaylist = resources
                .filterIsInstance<Resource.Success<MutableList<Song>>>()
                .map { it.value }
                .flatten()
                .toMutableList()

            playlist = newPlaylist
            state = State.INITIALIZED
        }

        return this
    }

    private suspend fun getLocalPlaylist(): Resource<MutableList<Song>> {
        val uri = MusicUtil.localMusicStorageUri().toString()
        val resource = withContext(Dispatchers.IO) {
            musicPlayListUsecase.getLocalSongList(uri)
        }

        return resource
    }

    private suspend fun getStreamingPlaylist(): Resource<MutableList<Song>> {
        val resource = withContext(Dispatchers.IO) {
            musicPlayListUsecase.getStreamingUrlList()
        }

        return resource
    }

    fun whenReady(onReady: (Boolean) -> Unit): Boolean =
        if (state == State.CREATED || state == State.INITIALIZING) {
            onReadyListeners += onReady
            false
        } else {
            onReady(state == State.INITIALIZED)
            true
        }

    private enum class State {
        CREATED, INITIALIZING, INITIALIZED, ERROR
    }
}