package com.jooheon.toyplayer.features.common.menu

import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaylistError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import javax.inject.Inject

class SongMenuHandler @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
) {
    suspend fun addToPlayingQueue(song: Song): Result<Playlist, PlaylistError> {
        return playlistUseCase
            .getPlayingQueue()
            .onSuccess { updatePlaylist(it, song) }
    }

    suspend fun addToPlaylist(playlist: Playlist, song: Song): Result<Unit, PlaylistError> {
        val name = playlist.name

        if(!playlistUseCase.checkValidName(name)) { // TODO: 중복 코드.. usecase로 옮겨야함
            return Result.Error(PlaylistError.DuplicatedName)
        }

        val result = playlistUseCase.nextPlaylistIdOrNull()?.let {
            playlistUseCase.insertPlaylists(
                playlist.copy(
                    id = it,
                    songs = listOf(song)
                )
            )
            Result.Success(Unit)
        } ?: run {
            Result.Error(PlaylistError.UnKnown)
        }

        return result
    }

    suspend fun updatePlaylist(playlist: Playlist, song: Song): Result<Unit, PlaylistError> {
        playlistUseCase.updatePlaylists( // FIXME
            playlist.copy(songs = playlist.songs + song)
        )

        return Result.Success(Unit)
    }
}