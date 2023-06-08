package com.jooheon.clean_architecture.domain.usecase.music.playlist

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.repository.MusicPlaylistRepository
import com.jooheon.clean_architecture.domain.usecase.music.playingqueue.MusicPlayingQueueUseCase.Companion.PlayingQueuePlaylistId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicPlaylistUseCase @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val musicPlaylistRepository: MusicPlaylistRepository,
): IMusicPlaylistUseCase {
    private val _playlistState = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistState = _playlistState.asStateFlow()

    init {
        update()
    }

    override fun update() {
        getAllPlaylist().onEach { resource ->
            when(resource) {
                is Resource.Success -> {
                    val list = resource.value.toMutableList().apply {
//                        removeIf { it.id == PlayingQueuePlaylistId }
                    }
                    _playlistState.tryEmit(list)
                }
                is Resource.Failure -> _playlistState.tryEmit(emptyList())
                else -> { /** Nothing **/ }
            }
        }.launchIn(applicationScope)
    }

    override fun getAllPlaylist(): Flow<Resource<List<Playlist>>> {
        return flow {

            emit(Resource.Loading)
            val resource = withContext(Dispatchers.IO) {
                musicPlaylistRepository.getAllPlaylist()
            }
            emit(resource)
        }
    }

    override fun getPlaylist(id: Int): Flow<Resource<Playlist>> {
        return flow {
            emit(Resource.Loading)
            val resource = withContext(Dispatchers.IO) {
                musicPlaylistRepository.getPlaylist(id)
            }
            emit(resource)
        }
    }

    override suspend fun updatePlaylists(vararg playlist: Playlist) {
        musicPlaylistRepository.updatePlaylists(*playlist)
        update()
    }

    override suspend fun insertPlaylists(vararg playlist: Playlist) {
        musicPlaylistRepository.insertPlaylists(*playlist)
        update()
    }

    override suspend fun deletePlaylists(vararg playlist: Playlist) {
        musicPlaylistRepository.deletePlaylists(*playlist)
        update()
    }
}