package com.jooheon.toyplayer.features.artist.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailEvent
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailUiState
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
class ArtistDetailViewModel @Inject constructor(
    playlistUseCase: PlaylistUseCase,
    private val playerController: PlayerController,
    private val songMenuHandler: SongMenuHandler,
): ViewModel() {
    private val artistFlow = MutableStateFlow(Artist.default)
    val uiState: StateFlow<ArtistDetailUiState> =
        combine(
            artistFlow,
            playlistUseCase.flowAllPlaylists()
        ) { artist, playlists ->
            artist to playlists
        }.map { (artist, playlists) ->
            ArtistDetailUiState(
                artist = artist,
                playlists = playlists.withOutDefault(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistDetailUiState.default
        )

    internal fun loadArtist(context: Context, artistId: String) = viewModelScope.launch {
        artistFlow.emit(getArtist(context, artistId))
    }

    internal fun dispatch(event: ArtistDetailEvent) = viewModelScope.launch {
        when(event) {
            is ArtistDetailEvent.OnSongClick -> onSongClick(event.song, true)
            is ArtistDetailEvent.OnAddPlayingQueue -> onSongClick(event.song, false)
            is ArtistDetailEvent.OnAddPlaylist -> {
                if(event.playlist.id == Playlist.default.id) {
                    songMenuHandler.make(event.playlist, listOf(event.song))
                } else {
                    songMenuHandler.insert(event.playlist.id, listOf(event.song))
                }
            }

            is ArtistDetailEvent.OnNavigateAlbum -> { /** nothing **/ }
        }
    }

    private suspend fun onSongClick(song: Song, playWhenReady: Boolean) {
        val artist = uiState.value.artist
        playerController.enqueue(
            mediaId = MediaId.Artist(artist.id),
            song = song,
            playWhenReady = playWhenReady
        )
        val event = SnackbarEvent(UiText.StringResource(Strings.add))
        SnackbarController.sendEvent(event)
    }

    private suspend fun getArtist(
        context: Context,
        artistId: String
    ): Artist = withContext(Dispatchers.IO) {
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

        return@withContext artist
    }
}