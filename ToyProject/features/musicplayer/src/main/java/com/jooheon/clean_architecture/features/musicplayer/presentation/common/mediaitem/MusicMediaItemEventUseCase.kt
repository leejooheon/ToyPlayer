package com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.playlist.PlaylistUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicMediaItemEventUseCase @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val playlistUseCase: PlaylistUseCase,
) {

    private val _playlistState = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistState = _playlistState.asStateFlow()

    init {
        loadData()
    }
    private fun loadData() {
        playlistUseCase.getAllPlaylist()
            .onEach { resource ->
                if(resource is Resource.Success) {
                    _playlistState.tryEmit(resource.value)
                }
            }.launchIn(applicationScope)
    }

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
                    playlistUseCase.updatePlaylists(updatedPlaylist)
                }
                loadData()
            }
            is MusicMediaItemEvent.OnAddToPlayingQueueClick -> {}
            is MusicMediaItemEvent.OnTagEditorClick -> {}
            else -> { /** Nothing **/}
        }
    }
}