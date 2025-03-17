package com.jooheon.toyplayer.features.library.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.library.main.model.LibraryEvent
import com.jooheon.toyplayer.features.library.main.model.LibraryUiState
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
class LibraryViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val playerController: PlayerController,
): ViewModel() {
    private val _uiState = MutableStateFlow(LibraryUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun loadData(context: Context) = viewModelScope.launch {
        val artists = getArtists(context)
        _uiState.update {
            it.copy(
                artists = artists,
            )
        }

        playlistUseCase.getAllPlaylist()
            .onSuccess { playlists ->
                _uiState.update {
                    it.copy(
                        defaultPlaylists = playlists
                    )
                }
            }
    }

    private suspend fun getArtists(
        context: Context
    ): List<Artist> = withContext(Dispatchers.IO) {
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

        val groupByAlbum = musicList.groupBy {
            it.albumId
        }.map { (albumId, songs) ->
            Album(
                id = albumId,
                name = songs.firstOrNull()?.album.defaultEmpty(),
                artist = songs.firstOrNull()?.artist.defaultEmpty(),
                artistId = songs.firstOrNull()?.artistId.defaultEmpty(),
                imageUrl = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                songs = songs.sortedBy { it.trackNumber }
            )
        }.groupBy {
            it.artistId
        }

        val groupByArtist = musicList.groupBy {
            it.artistId
        }.map { (artistId, songs) ->
            com.jooheon.toyplayer.domain.model.music.Artist(
                id = artistId,
                name = songs.firstOrNull()?.artist.defaultEmpty(),
                albums = groupByAlbum[artistId] ?: emptyList()
            )
        }

        return@withContext groupByArtist
    }
}