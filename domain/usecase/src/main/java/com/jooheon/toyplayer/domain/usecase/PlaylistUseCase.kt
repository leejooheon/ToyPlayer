package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend fun getAllPlaylist(): Result<List<Playlist>, RootError> = withContext(Dispatchers.IO) {
        playlistRepository.getAllPlaylist()
    }

    suspend fun getPlaylist(playlistId: Int): Result<Playlist, RootError> = withContext(Dispatchers.IO) {
        return@withContext playlistRepository.getPlaylist(playlistId)
    }

    suspend fun insertPlaylists(vararg playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.insertPlaylists(*playlist)
    }

    suspend fun deletePlaylists(vararg playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.deletePlaylists(*playlist)
    }

    suspend fun updatePlaylists(vararg playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.updatePlaylists(*playlist)
    }

    suspend fun checkValidName(name: String): Boolean = withContext(Dispatchers.IO) {
        if(name.isBlank()) return@withContext false

        val result = getAllPlaylist()
        return@withContext when(result) {
            is Result.Success -> name !in result.data.map { it.name }
            is Result.Error -> false
        }
    }

    suspend fun checkValidId(id: Int): Boolean = withContext(Dispatchers.IO) {
        if(id in Playlist.defaultPlaylistIds.map { it.first }) return@withContext false

        val result = getAllPlaylist()
        return@withContext when(result) {
            is Result.Success -> id in result.data.map { it.id }
            is Result.Error -> false
        }
    }

    suspend fun nextPlaylistIdOrNull(): Int? = withContext(Dispatchers.IO) {
        val result = getAllPlaylist()
        return@withContext when(result) {
            is Result.Success -> result.data.maxOf { it.id } + 1
            is Result.Error -> null
        }
    }
}