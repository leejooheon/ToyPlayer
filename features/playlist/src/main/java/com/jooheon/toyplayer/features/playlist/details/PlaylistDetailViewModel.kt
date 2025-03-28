package com.jooheon.toyplayer.features.playlist.details

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gun0912.tedpermission.coroutine.TedPermission
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.bitmap.loadGlideBitmap
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.controller.SnackbarEvent
import com.jooheon.toyplayer.features.common.permission.audioStoragePermission
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailEvent
import com.jooheon.toyplayer.features.playlist.details.model.PlaylistDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.roundToInt

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    private val idChannel = Channel<Int>()

    val uiState: StateFlow<PlaylistDetailUiState> =
        idChannel.receiveAsFlow()
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
            is PlaylistDetailEvent.OnPlay -> onPlay(event.index)
            is PlaylistDetailEvent.OnPlayAll -> onPlayAll(event.shuffle)
            is PlaylistDetailEvent.OnDelete -> onDelete(event.song)
            is PlaylistDetailEvent.OnPermissionRequest -> onPermissionRequest(event.context)
            is PlaylistDetailEvent.OnThumbnailImageSelected -> onThumbnailImageSelected(event.context, event.uri)
        }
    }

    internal fun loadData(id: Int) = viewModelScope.launch {
        idChannel.send(id)
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
        onPlay(startIndex)
    }

    private suspend fun onPlay(startIndex: Int) {
        val playlist = uiState.value.playlist

        playlistUseCase.insert(
            id = Playlist.PlayingQueue.id,
            songs = playlist.songs,
            reset = true,
        ).onSuccess {
            defaultSettingsUseCase.setLastEnqueuedPlaylistName(playlist.name)
            playerController.enqueue(
                songs = it.songs,
                startIndex = startIndex,
                playWhenReady = true,
            )
            val event = SnackbarEvent(UiText.StringResource(Strings.update))
            SnackbarController.sendEvent(event)
        }.onError {
            val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
            SnackbarController.sendEvent(event)
        }
    }

    private suspend fun onThumbnailImageSelected(
        context: Context,
        imageUri: Uri
    ) {
        saveImageToFile(context, imageUri)?.let {
            val playlist = uiState.value.playlist
            playlistUseCase.updateThumbnailImage(playlist.id, it.absolutePath)
            idChannel.send(playlist.id)
        } ?: run {
            val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
            SnackbarController.sendEvent(event)
        }
    }

    private suspend fun saveImageToFile(
        context: Context,
        imageUri: Uri
    ): File? = withContext(Dispatchers.IO) {
        try {
            val size = (256 * context.resources.displayMetrics.density).roundToInt()
            val bitmap: Bitmap? = try {
                suspendCancellableCoroutine { continuation ->
                    loadGlideBitmap(
                        context = context,
                        uri = imageUri,
                        size = Size(size, size),
                        onDone =  { continuation.resume(it) }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if(bitmap == null) {
                return@withContext null
            }

            val file = File(context.cacheDir, "playlist_cover_image_${imageUri.pathSegments.last()}.jpg")
            if (file.exists()) file.delete()
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            return@withContext file
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}