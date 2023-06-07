package com.jooheon.clean_architecture.domain.usecase.playlist

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.repository.PlaylistRepository
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class PlaylistUseCase @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val playlistRepository: PlaylistRepository,
): IPlaylistUseCase {
    private val _playlistState = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistState = _playlistState.asStateFlow()

    init {
        update()
    }

    override fun update() {
        getAllPlaylist().onEach { resource ->
            when(resource) {
                is Resource.Success -> _playlistState.tryEmit(resource.value)
                is Resource.Failure -> _playlistState.tryEmit(emptyList())
                else -> { /** Nothing **/ }
            }
        }.launchIn(applicationScope)
    }

    override fun getAllPlaylist(): Flow<Resource<List<Playlist>>> {
        return flow {

            emit(Resource.Loading)
            val resource = withContext(Dispatchers.IO) {
                playlistRepository.getAllPlaylist()
            }
            emit(resource)
        }
    }

    override fun getPlaylist(id: Int): Flow<Resource<Playlist>> {
        return flow {
            emit(Resource.Loading)
            val resource = withContext(Dispatchers.IO) {
                playlistRepository.getPlaylist(id)
            }
            emit(resource)
        }
    }

    override suspend fun updatePlaylists(vararg playlist: Playlist) {
        playlistRepository.updatePlaylists(*playlist)
        update()
    }

    override suspend fun insertPlaylists(vararg playlist: Playlist) {
        playlistRepository.insertPlaylists(*playlist)
        update()
    }

    override suspend fun deletePlaylists(vararg playlist: Playlist) {
        playlistRepository.deletePlaylists(*playlist)
        update()
    }
}