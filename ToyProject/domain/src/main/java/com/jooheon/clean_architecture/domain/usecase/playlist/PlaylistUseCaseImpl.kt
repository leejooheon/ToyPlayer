package com.jooheon.clean_architecture.domain.usecase.playlist

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistUseCase {
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
    }

    override suspend fun insertPlaylists(vararg playlist: Playlist) {
        playlistRepository.insertPlaylists(*playlist)
    }

    override suspend fun deletePlaylists(vararg playlist: Playlist) {
        playlistRepository.deletePlaylists(*playlist)
    }
}