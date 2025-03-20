package com.jooheon.toyplayer.features.album.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.errors.PlaylistError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.album.details.model.AlbumDetailEvent
import com.jooheon.toyplayer.features.album.details.model.AlbumDetailUiState
import com.jooheon.toyplayer.features.common.menu.SongMenuHandler
import com.jooheon.toyplayer.features.commonui.controller.SnackbarController
import com.jooheon.toyplayer.features.commonui.controller.SnackbarEvent
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
    private val playerController: PlayerController,
    private val songMenuHandler: SongMenuHandler,
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumDetailUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun loadData(
        context: Context,
        albumId: String
    ) = viewModelScope.launch {
        playlistUseCase.getAllPlaylist()
            .onSuccess { playlists ->
                _uiState.update {
                    it.copy(
                        playlists = playlists
                            .filterNot { // 기본 재생목록은 제외함
                                it.id in Playlist.defaultPlaylistIds.map { it.first }
                            }
                    )
                }
            }

        val album = getAlbums(context, albumId)
        _uiState.update {
            it.copy(
                album = album,
            )
        }
    }

    internal fun dispatch(event: AlbumDetailEvent) = viewModelScope.launch {
        when(event) {
            is AlbumDetailEvent.OnPlayAllClick -> onPlayAll(event.shuffle)
            is AlbumDetailEvent.OnSongClick -> onSongClick(event.song)
            is AlbumDetailEvent.OnAddPlayingQueue -> addToPlayingQueue(event.song)
            is AlbumDetailEvent.OnAddPlaylist -> {
                if(event.playlist.id == Playlist.default.id) {
                    addToPlaylist(event.playlist, event.song)
                } else {
                    updatePlaylist(event.playlist, event.song)
                }
            }
        }
    }

    private suspend fun onPlayAll(shuffle: Boolean) {
        val album = uiState.value.album

        val startIndex = if(shuffle) (album.songs.indices).random() else 0

        defaultSettingsUseCase.setLastEnqueuedPlaylistName(album.name)
        playerController.enqueue(
            songs = album.songs,
            startIndex = startIndex,
            playWhenReady = true,
        )
        playerController.shuffle(shuffle)
    }

    private suspend fun onSongClick(song: Song) {
        playerController.enqueue(
            song = song,
            playWhenReady = true
        )
        val event = SnackbarEvent(UiText.StringResource(Strings.add))
        SnackbarController.sendEvent(event)
    }

    private suspend fun addToPlayingQueue(song: Song) {
        songMenuHandler
            .addToPlayingQueue(song)
            .onSuccess {
                val event = SnackbarEvent(UiText.StringResource(Strings.add))
                SnackbarController.sendEvent(event)
            }
            .onError {
                val event = SnackbarEvent(UiText.StringResource(Strings.some_error))
                SnackbarController.sendEvent(event)
            }
    }

    private suspend fun addToPlaylist(playlist: Playlist, song: Song) {
        songMenuHandler
            .addToPlaylist(playlist, song)
            .onSuccess {
                val event = SnackbarEvent(UiText.StringResource(Strings.playlist_inserted))
                SnackbarController.sendEvent(event)
            }
            .onError {
                when(it) {
                    is PlaylistError.DuplicatedName -> {
                        val event = SnackbarEvent(UiText.StringResource(Strings.error_playlist, playlist.name))
                        SnackbarController.sendEvent(event)
                    }
                    else -> {
                        val event = SnackbarEvent(UiText.StringResource(Strings.some_error))
                        SnackbarController.sendEvent(event)
                    }
                }
            }
    }

    private suspend fun updatePlaylist(playlist: Playlist, song: Song) {
        songMenuHandler
            .updatePlaylist(playlist, song)
            .onSuccess {
                val event = SnackbarEvent(UiText.StringResource(Strings.add))
                SnackbarController.sendEvent(event)
            }
            .onError {
                val event = SnackbarEvent(UiText.StringResource(Strings.some_error))
                SnackbarController.sendEvent(event)
            }
    }

    private suspend fun getAlbums(
        context: Context,
        albumId: String,
    ): Album = withContext(Dispatchers.IO) {
        val musicList = suspendCancellableCoroutine { continuation ->
            playerController.getMusicListFuture(
                context = context,
                mediaId = MediaId.AllSongs,
                listener = { mediaItems ->
                    val songs = mediaItems.map { it.toSong() }
                    continuation.resume(songs)
                }
            )
        }

        val album = musicList
            .filter { it.albumId == albumId }
            .run {
                Album(
                    id = albumId,
                    name = this.firstOrNull()?.album.defaultEmpty(),
                    artist = this.firstOrNull()?.artist.defaultEmpty(),
                    artistId = this.firstOrNull()?.artistId.defaultEmpty(),
                    imageUrl = this.firstOrNull()?.imageUrl.defaultEmpty(),
                    songs = this.sortedBy { it.trackNumber }
                )
            }

        return@withContext album
    }
}