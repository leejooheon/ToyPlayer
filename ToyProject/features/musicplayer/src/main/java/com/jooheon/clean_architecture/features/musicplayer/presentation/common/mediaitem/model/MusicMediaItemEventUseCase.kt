package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicMediaItemEventUseCase @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
) {
    suspend fun dispatch(event: MusicMediaItemEvent) {
        when(event) {
            is MusicMediaItemEvent.OnAddPlaylistClick -> {
                val playlist = event.playlist ?: return

                val newSongs = playlist.songs.toMutableList().apply {
                    add(event.song)
                }

                val updatedPlaylist = playlist.copy(
                    songs =  newSongs
                )

                withContext(Dispatchers.IO) {
                    playlistUseCase.updatePlaylists(updatedPlaylist)
                }
            }
            is MusicMediaItemEvent.OnAddToPlayingQueueClick -> {}
            is MusicMediaItemEvent.OnTagEditorClick -> {}
            else -> { /** Nothing **/}
        }
    }

    suspend fun dispatch(event: MusicPlaylistItemEvent) {
        when(event) {
            is MusicPlaylistItemEvent.OnDelete -> deletePlaylist(event.playlist)
            is MusicPlaylistItemEvent.OnChangeName -> updatePlaylist(event.playlist)
            is MusicPlaylistItemEvent.OnSaveAsFile -> playlistSaveAsFile(event.playlist)
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