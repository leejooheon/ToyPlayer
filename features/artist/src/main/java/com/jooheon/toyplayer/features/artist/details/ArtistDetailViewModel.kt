package com.jooheon.toyplayer.features.artist.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailEvent
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailUiState
import com.jooheon.toyplayer.features.common.compose.SnackbarController
import com.jooheon.toyplayer.features.common.compose.SnackbarEvent
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    private val _uiState = MutableStateFlow(ArtistDetailUiState.default)
    internal val uiState = _uiState.asStateFlow()

    internal fun loadData(context: Context, artistId: String) = viewModelScope.launch {
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

        val artist = getArtist(context, artistId)
        _uiState.update {
            it.copy(
                artist = artist,
            )
        }
    }

    internal fun dispatch(event: ArtistDetailEvent) = viewModelScope.launch {
        when(event) {
            is ArtistDetailEvent.OnSongClick -> onSongClick(event.song)
            is ArtistDetailEvent.OnAddPlayingQueue -> addToPlayingQueue(event.song)
            is ArtistDetailEvent.OnAddPlaylist -> {
                if(event.playlist.id == Playlist.default.id) {
                    addToPlaylist(event.playlist, event.song)
                } else {
                    updatePlaylist(event.playlist, event.song)
                }
            }

            is ArtistDetailEvent.OnAlbumClick -> { /** nothing **/ }
        }
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
        playlistUseCase
            .getPlaylist(Playlist.PlayingQueuePlaylistId.first)
            .onSuccess {
                updatePlaylist(it, song)
                val event = SnackbarEvent(UiText.StringResource(Strings.update))
                SnackbarController.sendEvent(event)
            }
            .onError {
                val event = SnackbarEvent(UiText.StringResource(Strings.some_error))
                SnackbarController.sendEvent(event)
            }
    }

    private suspend fun addToPlaylist(playlist: Playlist, song: Song) {
        val name = playlist.name

        if(!playlistUseCase.checkValidName(name)) { // TODO: 중복 코드.. usecase로 옮겨야함
            val event = SnackbarEvent(UiText.StringResource(Strings.error_playlist, name))
            SnackbarController.sendEvent(event)
            return
        }

        playlistUseCase.nextPlaylistIdOrNull()?.let {
            playlistUseCase.insertPlaylists(
                playlist.copy(
                    id = it,
                    songs = listOf(song)
                )
            )
            val event = SnackbarEvent(UiText.StringResource(Strings.playlist_inserted))
            SnackbarController.sendEvent(event)
        } ?: run {
            val event = SnackbarEvent(UiText.StringResource(Strings.some_error))
            SnackbarController.sendEvent(event)
        }
    }

    private suspend fun updatePlaylist(playlist: Playlist, song: Song) {
        playlistUseCase.updatePlaylists(
            playlist.copy(songs = playlist.songs + song)
        )
    }

    private suspend fun getArtist(context: Context, artistId: String): Artist {
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

        val albums = musicList
            .filter { it.artistId == artistId }
            .groupBy { it.albumId }
            .map { (albumId, songs) ->
                Album(
                    id = albumId,
                    name = songs.firstOrNull()?.album.defaultEmpty(),
                    artist = songs.firstOrNull()?.artist.defaultEmpty(),
                    artistId = songs.firstOrNull()?.artistId.defaultEmpty(),
                    imageUrl = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                    songs = songs.sortedBy { it.trackNumber }
                )
            }

        val artist = musicList
            .firstOrNull { it.artistId == artistId }
            .run {
                Artist(
                    id = artistId,
                    name = this?.artist.defaultEmpty(),
                    albums = albums
                )
            }

        return artist
    }
}