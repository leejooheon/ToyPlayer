package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.repository.api.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistUseCase @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) {
    suspend fun getAllPlaylist(): Result<List<Playlist>, PlaybackDataError> = withContext(Dispatchers.IO) {
        playlistRepository.getAllPlaylist()
    }

    suspend fun getPlaylist(id: Int): Result<Playlist, PlaybackDataError> = withContext(Dispatchers.IO) {
        return@withContext playlistRepository.getPlaylist(id)
    }

    fun flowAllPlaylists(): Flow<List<Playlist>> = playlistRepository.flowAllPlaylists()

    fun flowPlaylist(id: Int): Flow<Playlist?> = playlistRepository.flowPlaylist(id)

    suspend fun makeDefaultPlaylist(
        id: Int, // 바꾸자
        playlist: Playlist,
        songs: List<Song>
    ) = withContext(Dispatchers.IO) {
        if(id !in Playlist.defaultPlaylists.map { it.id }) throw IllegalArgumentException("Invalid playlist id: $id")
        playlistRepository.insertPlaylist(playlist.copy(id = id))
        insert(id, songs, true)
    }

    suspend fun make(playlist: Playlist, songs: List<Song>): Result<Unit, PlaybackDataError> = withContext(Dispatchers.IO) {
        if(!checkValidName(playlist.name)) {
            return@withContext Result.Error(PlaybackDataError.PlaylistDuplicatedName)
        }

        val id = nextPlaylistIdOrNull().defaultZero()
        playlistRepository.insertPlaylist(playlist.copy(id = id))
        val result = insert(id, songs, true)

        return@withContext when(result) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
        }
    }

    suspend fun favorite(
        id: Int,
        song: Song,
    ): Result<Unit, PlaybackDataError> = withContext(Dispatchers.IO) {
        val isFavorite = !song.isFavorite

        getPlaylist(id).onSuccess { playlist ->
            playlist.songs.indexOf(song).takeIf { it >= 0 }?.let { index ->
                val updatedSongs = playlist.songs
                    .toMutableList()
                    .apply {
                        set(
                            index = index,
                            element = song.copy(
                                trackNumber = index + 1,
                                isFavorite = isFavorite
                            )
                        )
                    }

                playlistRepository.updatePlaylist(playlist.copy(songs = updatedSongs))
            }
        }.onError {
            return@withContext Result.Error(it)
        }

        val result = getPlaylist(Playlist.Favorite.id).onSuccess { playlist ->
            val updatedSongs = playlist.songs
                .toMutableList()
                .apply { if (!isFavorite) remove(song) else add(song.copy(isFavorite = true)) }
                .mapIndexed { index, song -> song.copy(trackNumber = index + 1) }
            playlistRepository.updatePlaylist(playlist.copy(songs = updatedSongs))
        }

        return@withContext when(result) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
        }
    }

    suspend fun insert(
        id: Int,
        songs: List<Song>,
        reset: Boolean,
    ): Result<Playlist, PlaybackDataError> = withContext(Dispatchers.IO) {
        val result = getPlaylist(id)

        return@withContext when(result) {
            is Result.Success -> {
                val playlist = result.data
                var index = if(reset) 1 else playlist.songs.maxOfOrNull { it.trackNumber }.defaultZero() + 1
                val newSongs = songs.map { it.copy(trackNumber = index++) }
                val newPlaylist = playlist.copy(
                    songs = if(reset) newSongs else playlist.songs + newSongs
                )
                playlistRepository.updatePlaylist(newPlaylist)
                Result.Success(newPlaylist)
            }
            is Result.Error -> result
        }
    }

    suspend fun delete(
        id: Int,
        song: Song,
    ): Result<Unit, PlaybackDataError> = withContext(Dispatchers.IO) {
        val result = getPlaylist(id)

        return@withContext when(result) {
            is Result.Success -> {
                val playlist = result.data
                playlistRepository.updatePlaylist(
                    playlist.copy(
                        songs = playlist.songs.filterNot { it.trackNumber == song.trackNumber }
                    )
                )
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }
    suspend fun updateThumbnailImage(
        id: Int,
        url: String
    ): Result<Unit, PlaybackDataError> = withContext(Dispatchers.IO) {
        val result = getPlaylist(id)

        return@withContext when(result) {
            is Result.Success -> {
                val playlist = result.data
                playlistRepository.updatePlaylist(playlist.copy(thumbnailUrl = url))
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }

    suspend fun updateName(id: Int, name: String): Result<Unit, PlaybackDataError> = withContext(Dispatchers.IO) {
        if(!checkValidName(name)) {
            return@withContext Result.Error(PlaybackDataError.PlaylistDuplicatedName)
        }

        val result = getPlaylist(id)

        return@withContext when(result) {
            is Result.Success -> {
                val playlist = result.data
                playlistRepository.updatePlaylist(playlist.copy(name = name))
                Result.Success(Unit)
            }
            is Result.Error -> result
        }
    }
    suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistRepository.deletePlaylist(playlist)
    }
    suspend fun getPlayingQueue(): Result<Playlist, PlaybackDataError> {
        return getPlaylist(Playlist.PlayingQueue.id)
    }

    private suspend fun checkValidName(name: String): Boolean = withContext(Dispatchers.IO) {
        if(name.isBlank()) return@withContext false

        val result = getAllPlaylist()
        return@withContext when(result) {
            is Result.Success -> name !in result.data.map { it.name }
            is Result.Error -> false
        }
    }

    private suspend fun nextPlaylistIdOrNull(): Int? = withContext(Dispatchers.IO) {
        val result = getAllPlaylist()
        return@withContext when(result) {
            is Result.Success -> result.data.maxOf { it.id } + 1
            is Result.Error -> null
        }
    }
}