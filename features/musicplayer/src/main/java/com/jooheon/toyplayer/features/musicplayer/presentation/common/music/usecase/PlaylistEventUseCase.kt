package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase

import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.PlaylistEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PlaylistEventUseCase(
    private val playlistUseCase: PlaylistUseCase,
) {
    suspend fun dispatch(event: PlaylistEvent) {
        when(event) {
            is PlaylistEvent.OnDelete -> deletePlaylist(event.playlist)
            is PlaylistEvent.OnChangeName -> updatePlaylist(event.playlist)
            is PlaylistEvent.OnSaveAsFile -> playlistSaveAsFile(event.playlist)
            else -> { /** Nothing **/ }
        }
    }

    private suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistUseCase.deletePlaylists(playlist)
    }

    private suspend fun updatePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        playlistUseCase.updatePlaylists(playlist)
    }

    private suspend fun playlistSaveAsFile(playlist: Playlist) = withContext(Dispatchers.IO) {
        /** TODO **/
        delay(300)
    }
}