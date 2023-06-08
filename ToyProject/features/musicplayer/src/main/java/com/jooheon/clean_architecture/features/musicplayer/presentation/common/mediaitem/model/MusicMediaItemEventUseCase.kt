package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model

import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.music.playlist.MusicPlaylistUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicMediaItemEventUseCase @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val musicPlaylistUseCase: MusicPlaylistUseCase,
) {
    fun dispatch(event: MusicMediaItemEvent) = applicationScope.launch {
        when(event) {
            is MusicMediaItemEvent.OnAddPlaylistClick -> {
                val playlist = event.playlist ?: return@launch

                val newSongs = playlist.songs.toMutableList().apply {
                    add(event.song)
                }

                val updatedPlaylist = playlist.copy(
                    songs =  newSongs
                )

                withContext(Dispatchers.IO) {
                    musicPlaylistUseCase.updatePlaylists(updatedPlaylist)
                }
            }
            is MusicMediaItemEvent.OnAddToPlayingQueueClick -> {}
            is MusicMediaItemEvent.OnTagEditorClick -> {}
            else -> { /** Nothing **/}
        }
    }

    fun dispatch(event: MusicPlaylistItemEvent) = applicationScope.launch {
        when(event) {
            is MusicPlaylistItemEvent.OnDelete -> deletePlaylist(event.playlist)
            is MusicPlaylistItemEvent.OnChangeName -> updatePlaylist(event.playlist)
            is MusicPlaylistItemEvent.OnSaveAsFile -> playlistSaveAsFile(event.playlist)
            else -> { /** Nothing **/ }
        }
    }

    private suspend fun deletePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        musicPlaylistUseCase.deletePlaylists(playlist)
    }

    private suspend fun updatePlaylist(playlist: Playlist) = withContext(Dispatchers.IO) {
        musicPlaylistUseCase.updatePlaylists(playlist)
    }

    private suspend fun playlistSaveAsFile(playlist: Playlist) = withContext(Dispatchers.IO) {
        /** TODO **/
        delay(300)
    }
}