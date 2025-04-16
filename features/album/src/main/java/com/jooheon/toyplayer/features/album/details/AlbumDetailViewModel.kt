package com.jooheon.toyplayer.features.album.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
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
import com.jooheon.toyplayer.features.common.extension.withOutDefault
import com.jooheon.toyplayer.features.common.menu.SongMenuHandler
import com.jooheon.toyplayer.features.common.controller.SnackbarController
import com.jooheon.toyplayer.features.common.controller.SnackbarEvent
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val albumFlow = MutableStateFlow(Album.default)
    val uiState: StateFlow<AlbumDetailUiState> =
        combine(
            albumFlow,
            playlistUseCase.flowAllPlaylists()
        ) { album, playlists ->
            album to playlists
        }.map { (album, playlists) ->
            AlbumDetailUiState(
                album = album,
                playlists = playlists.withOutDefault(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlbumDetailUiState.default
        )

    internal fun loadAlbum(
        context: Context,
        albumId: String
    ) = viewModelScope.launch {
        albumFlow.emit(getAlbums(context, albumId))
    }

    internal fun dispatch(event: AlbumDetailEvent) = viewModelScope.launch {
        when(event) {
            is AlbumDetailEvent.OnPlayAllClick -> onPlayAll(event.shuffle)
            is AlbumDetailEvent.OnSongClick -> onPlay(event.index, true)
            is AlbumDetailEvent.OnAddPlayingQueue -> onSongClick(event.song, false)
            is AlbumDetailEvent.OnAddPlaylist -> {
                if(event.playlist.id == Playlist.default.id) {
                    songMenuHandler.make(event.playlist, listOf(event.song))
                } else {
                    songMenuHandler.insert(event.playlist.id, listOf(event.song))
                }
            }
        }
    }

    private suspend fun onPlayAll(shuffle: Boolean) {
        val album = uiState.value.album
        val startIndex = if(shuffle) (album.songs.indices).random() else 0
        onPlay(startIndex, true)
        if(shuffle) playerController.shuffle(true)
    }

    private suspend fun onPlay(startIndex: Int, playWhenReady: Boolean) {
        val album = uiState.value.album
        playlistUseCase.insert(
            id = Playlist.PlayingQueue.id,
            songs = album.songs,
            reset = true,
        ).onSuccess {
            defaultSettingsUseCase.setLastEnqueuedPlaylistName(album.name)
            playerController.enqueue(
                songs = it.songs,
                startIndex = startIndex,
                playWhenReady = playWhenReady,
            )
            val event = SnackbarEvent(UiText.StringResource(Strings.update))
            SnackbarController.sendEvent(event)
        }.onError {
            val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
            SnackbarController.sendEvent(event)
        }
    }

    private suspend fun onSongClick(song: Song, playWhenReady: Boolean) {
        playlistUseCase.insert(
            id = Playlist.PlayingQueue.id,
            songs = listOf(song),
            reset = false,
        ).onSuccess {
            playerController.enqueue(
                songs = it.songs,
                startIndex = it.songs.lastIndex,
                playWhenReady = playWhenReady,
            )
            val event = SnackbarEvent(UiText.StringResource(Strings.add))
            SnackbarController.sendEvent(event)
        }.onError {
            val event = SnackbarEvent(UiText.StringResource(Strings.error_default))
            SnackbarController.sendEvent(event)
        }
    }

    private suspend fun getAlbums(
        context: Context,
        albumId: String,
    ): Album = withContext(Dispatchers.IO) {
        val musicList: List<Song> = suspendCancellableCoroutine { continuation ->
            playerController.getMusicListFuture(
                context = context,
                mediaId = MediaId.AllSongs,
                listener = { result ->
                    result.onSuccess {
                        val songs = it.map { it.toSong() }
                        continuation.resume(songs)
                    }.onError {
                        continuation.resume(emptyList())
                    }
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