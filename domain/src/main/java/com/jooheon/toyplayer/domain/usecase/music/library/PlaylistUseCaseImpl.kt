package com.jooheon.toyplayer.domain.usecase.music.library

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.repository.library.PlaylistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    override suspend fun update() {
        val resource = withContext(Dispatchers.IO) {
            playlistRepository.getAllPlaylist()
        }
        when(resource) {
            is Resource.Success -> _allPlaylistState.tryEmit(resource.value)
            else -> _allPlaylistState.tryEmit(emptyList())
        }
    }
}