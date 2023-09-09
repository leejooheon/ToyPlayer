package com.jooheon.clean_architecture.domain.usecase.music.library

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.repository.library.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class PlaylistUseCaseImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistUseCase {
    private val _allPlaylistState = MutableStateFlow<List<Playlist>>(emptyList())
    override fun allPlaylist() = _allPlaylistState.asStateFlow()

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

    private suspend fun update() {
        val resource = withContext(Dispatchers.IO) {
            playlistRepository.getAllPlaylist()
        }
        when(resource) {
            is Resource.Success -> _allPlaylistState.tryEmit(resource.value)
            else -> _allPlaylistState.tryEmit(emptyList())
        }
    }
}