package com.jooheon.toyplayer.features.playlist.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gun0912.tedpermission.coroutine.TedPermission
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.permission.audioStoragePermission
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    private val idFlow = MutableStateFlow(-1)
    val uiState: StateFlow<PlaylistDetailUiState> =
        idFlow
            .flatMapLatest { playlistUseCase.flowPlaylist(it) }
            .map {
                val playlist = it.default(Playlist.default)
                val permissionResult = TedPermission.create()
                    .setPermissions(audioStoragePermission)
                    .check()

                PlaylistDetailUiState(
                    playlist = playlist,
                    requirePermission = playlist.id == Playlist.Local.id && !permissionResult.isGranted,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlaylistDetailUiState.default
            )

    internal fun dispatch(
        event: PlaylistDetailEvent
    ) = viewModelScope.launch {
        when(event) {
            is PlaylistDetailEvent.OnPlayAllClick -> onPlayAll(event.shuffle)
            is PlaylistDetailEvent.OnDelete -> onDelete(event.song)
            is PlaylistDetailEvent.OnPermissionRequest -> onPermissionRequest(event.context)
        }
    }

    internal fun loadData(id: Int) = viewModelScope.launch {
        idFlow.emit(id)
    }

    private suspend fun onPermissionRequest(context: Context) {
        val result = TedPermission.create()
            .setDeniedTitle(Strings.action_request_permission)
            .setDeniedMessage(Strings.description_permission_read_storage)
            .setPermissions(audioStoragePermission)
            .check()

        if(result.isGranted) {
            playerController.getMusicListFuture(
                context = context,
                mediaId = MediaId.Root,
                listener = { }
            )
        }
    }
    private suspend fun onDelete(song: Song) {
        val playlist = uiState.value.playlist
        playlistUseCase.delete(playlist.id, song)
    }

    private suspend fun onPlayAll(shuffle: Boolean) {
        val playlist = uiState.value.playlist

        val startIndex = if(shuffle) (playlist.songs.indices).random() else 0
        defaultSettingsUseCase.setLastEnqueuedPlaylistName(playlist.name)
        playerController.enqueue(
            mediaId = MediaId.Playlist(playlist.id),
            songs = playlist.songs,
            startIndex = startIndex,
            playWhenReady = true,
        )
        playerController.shuffle(shuffle)
    }
}