package com.jooheon.toyplayer.features.common.menu

import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.controller.SnackbarEvent
import javax.inject.Inject

class SongMenuHandler @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
) {
    suspend fun make(playlist: Playlist, songs: List<Song>) {
        val result = playlistUseCase.make(
            playlist = playlist,
            songs = songs
        )

        val event = when(result) {
            is Result.Success -> SnackbarEvent(UiText.StringResource(Strings.playlist_inserted))
            is Result.Error -> {
                when(result.error) {
                    is PlaybackDataError.PlaylistDuplicatedName -> SnackbarEvent(UiText.StringResource(Strings.error_playlist, playlist.name))
                    else -> SnackbarEvent(UiText.StringResource(Strings.error_default))
                }
            }
        }

        SnackbarController.sendEvent(event)
    }

    suspend fun insert(id: Int, songs: List<Song>) {
        val result = playlistUseCase.insert(
            id = id,
            songs = songs,
            reset = false,
        )

        val event = when(result) {
            is Result.Success -> SnackbarEvent(UiText.StringResource(Strings.add))
            is Result.Error -> SnackbarEvent(UiText.StringResource(Strings.error_default))
        }

        SnackbarController.sendEvent(event)
    }
}