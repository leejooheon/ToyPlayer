package com.jooheon.clean_architecture.features.musicplayer.presentation.artist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicArtistScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): BaseViewModel() {
    override val TAG = MusicArtistScreenViewModel::class.java.simpleName

    private val _musicArtistScreenState = MutableStateFlow(MusicArtistScreenState.default)
    val musicArtistScreenState = _musicArtistScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Artist>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    private val _playlistState = MutableStateFlow<List<Song>>(emptyList())
    private val playlistState = _playlistState.asStateFlow()

    init {
        collectMusicState()
        loadData()
    }

    fun dispatch(event: MusicArtistScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicArtistScreenEvent.OnArtistItemClick -> _navigateToDetailScreen.send(event.artist)
        }
    }

    fun loadData() = viewModelScope.launch {
        val playlistType = musicControllerUsecase.musicState.value.playlistType
        musicControllerUsecase.loadPlaylist(playlistType)
    }

    private fun collectMusicState() = viewModelScope.launch {
        musicControllerUsecase.musicState.collectLatest {
            _playlistState.tryEmit(it.playlist)
            updateArtists()
        }
    }

    private suspend fun updateArtists() = withContext(Dispatchers.IO) {
        val playlist = playlistState.value

        val groupByAlbum = playlist.groupBy {
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

        val groupByArtist = playlist.groupBy {
            it.artistId
        }.map { (artistId, songs) ->
            Artist(
                id = artistId,
                name = songs.firstOrNull()?.artist.defaultEmpty(),
                albums = groupByAlbum[artistId] ?: emptyList()
            )
        }

        _musicArtistScreenState.update {
            it.copy(artists = groupByArtist)
        }
    }
}