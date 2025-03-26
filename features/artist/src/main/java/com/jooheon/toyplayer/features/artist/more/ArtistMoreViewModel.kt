package com.jooheon.toyplayer.features.artist.more

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.Artist
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.artist.more.model.ArtistMoreUiState
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class ArtistMoreViewModel @Inject constructor(
    private val playerController: PlayerController,
): ViewModel() {
    private val artistsFlow = MutableStateFlow<List<Artist>>(emptyList())

    val uiState: StateFlow<ArtistMoreUiState> =
        artistsFlow
            .map { ArtistMoreUiState(artists = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ArtistMoreUiState.default,
            )

    internal fun loadArtists(context: Context) = viewModelScope.launch {
        artistsFlow.emit(getArtists(context))
    }

    private suspend fun getArtists(
        context: Context
    ): List<Artist> = withContext(Dispatchers.IO) {
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