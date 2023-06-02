package com.jooheon.clean_architecture.features.musicplayer.presentation.album

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.PlaylistType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicAlbumScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): BaseViewModel() {
    override val TAG = MusicAlbumScreenViewModel::class.java.simpleName

    private val _musicAlbumScreenState = MutableStateFlow(MusicAlbumScreenState.default)
    val musicAlbumScreenState = _musicAlbumScreenState.asStateFlow()

    private val _playlistState = MutableStateFlow<List<Song>>(emptyList())
    private val playlistState = _playlistState.asStateFlow()

    init {
        collectMusicState()
        loadData()
    }

    fun dispatch(musicAlbumScreenEvent: MusicAlbumScreenEvent) = viewModelScope.launch {

    }
    fun loadData() = viewModelScope.launch {
        val playlistType = musicControllerUsecase.musicState.value.playlistType
        musicControllerUsecase.loadPlaylist(playlistType)
    }

    private fun collectMusicState() = viewModelScope.launch {
        musicControllerUsecase.musicState.collectLatest {
            _playlistState.tryEmit(it.playlist)


            updateAlbums()
        }
    }

    private suspend fun updateAlbums() = withContext(Dispatchers.IO) {
        val playlist = playlistState.value

        val groupByAlbum = playlist.groupBy {
            it.albumId
        }.map { (albumId, songs) ->
            Album(
                id = albumId,
                name = songs.firstOrNull()?.album.defaultEmpty(),
                artist = songs.firstOrNull()?.artist.defaultEmpty(),
                artistId = songs.firstOrNull()?.artistId.defaultEmpty(),
                songs = songs
            )
        }

        _musicAlbumScreenState.update {
            it.copy(
                albums = groupByAlbum
            )
        }
    }

}