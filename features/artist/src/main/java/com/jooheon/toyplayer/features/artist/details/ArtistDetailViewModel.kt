package com.jooheon.toyplayer.features.artist.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.artist.details.model.ArtistDetailUiState
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
    val uiState = _uiState.asStateFlow()

    fun loadData(context: Context, artistId: String) = viewModelScope.launch {
        playlistUseCase.getAllPlaylist()
            .onSuccess { playlists ->
                _uiState.update {
                    it.copy(
                        playlists = playlists
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