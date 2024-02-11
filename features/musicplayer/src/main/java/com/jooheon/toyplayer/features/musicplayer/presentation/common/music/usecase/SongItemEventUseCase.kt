package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase

import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SongItemEventUseCase(
    private val playlistUseCase: PlaylistUseCase,
) {
    suspend fun dispatch(event: SongItemEvent) {
        when (event) {
            is SongItemEvent.OnAddPlaylistClick -> {
                val playlist = event.playlist ?: return

                val newSongs = playlist.songs.toMutableList().apply {
                    add(event.song)
                }

                val updatedPlaylist = playlist.copy(
                    songs = newSongs
                )

                withContext(Dispatchers.IO) {
                    playlistUseCase.updatePlaylists(updatedPlaylist)
                }
            }

            is SongItemEvent.OnAddToPlayingQueueClick -> {}
            is SongItemEvent.OnTagEditorClick -> {}
            else -> {
                /** Nothing **/
            }
        }
    }
}