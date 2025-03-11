package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend fun requireInitialize(): Boolean = withContext(Dispatchers.IO) {
        return@withContext (getAllPlaylist() as? Result.Success)?.data.defaultEmpty().isEmpty()
    }

    suspend fun getAllPlaylist(): Result<List<Playlist>, RootError> = withContext(Dispatchers.IO) {
        playlistRepository.getAllPlaylist()
    }

    suspend fun updatePlaylists(vararg playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.updatePlaylists(*playlist)
    }

    suspend fun insertPlaylists(vararg playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.insertPlaylists(*playlist)
    }

    suspend fun deletePlaylists(vararg playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.deletePlaylists(*playlist)
    }
    suspend fun getPlaylist(playlistId: Int): Result<Playlist, RootError> = withContext(Dispatchers.IO) {
        return@withContext playlistRepository.getPlaylist(playlistId)
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