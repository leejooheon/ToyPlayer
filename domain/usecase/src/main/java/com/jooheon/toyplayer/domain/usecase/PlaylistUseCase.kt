package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository
) {
    suspend fun getAllPlaylist() = playlistRepository.getAllPlaylist()

    suspend fun updatePlaylists(vararg playlist: Playlist) {
        playlistRepository.updatePlaylists(*playlist)
        update()
    }

    suspend fun insertPlaylists(vararg playlist: Playlist) {
        playlistRepository.insertPlaylists(*playlist)
        update()
    }

    suspend fun deletePlaylists(vararg playlist: Playlist) {
        playlistRepository.deletePlaylists(*playlist)
        update()
    }

    suspend fun update() {
        val resource = withContext(Dispatchers.IO) {
            playlistRepository.getAllPlaylist()
        }
//        when(resource) {
//            is Resource.Success -> _allPlaylistState.tryEmit(resource.value)
//            else -> _allPlaylistState.tryEmit(emptyList())
//        }
    }

    fun playingQueue() = flow {
        playlistRepository.getPlayingQueue()
            .onSuccess {
                emit(it)
            }
    }

    suspend fun setPlayingQueue(songs: List<Song>): Boolean {
        clear()
        playlistRepository.updatePlayingQueue(songs)

        return true
    }

    suspend fun clear() {
        playlistRepository.clear()
    }
}