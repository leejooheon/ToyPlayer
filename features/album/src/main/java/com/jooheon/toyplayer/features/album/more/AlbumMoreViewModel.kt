package com.jooheon.toyplayer.features.album.more

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.album.details.model.AlbumDetailUiState
import com.jooheon.toyplayer.features.album.more.model.AlbumMoreUiState
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
class AlbumMoreViewModel @Inject constructor(
    private val playerController: PlayerController,
): ViewModel() {
    private val _uiState = MutableStateFlow(AlbumMoreUiState.default)
    val uiState = _uiState.asStateFlow()

    internal fun loadData(context: Context, ) = viewModelScope.launch {
        val albums = getAlbums(context)

        _uiState.update {
            it.copy(
                albums = albums
            )
        }
    }

    private suspend fun getAlbums(
        context: Context,
    ): List<Album> = withContext(Dispatchers.IO) {
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
        }

        return@withContext groupByAlbum
    }
}