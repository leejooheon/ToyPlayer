package com.jooheon.toyplayer.features.album.details

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.album.details.model.AlbumDetailUiState
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class AlbumDetailViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val playerController: PlayerController,
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