package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.album.detail.model.MusicAlbumDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.SongItemEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicAlbumDetailScreenViewModel @Inject constructor(
    private val playlistUseCase: PlaylistUseCase,
    private val songItemEventUseCase: SongItemEventUseCase,
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG = MusicAlbumDetailScreenViewModel::class.java.simpleName

    private val _musicAlbumDetailScreenState = MutableStateFlow(MusicAlbumDetailScreenState.default)
    val musicAlbumDetailScreenState = _musicAlbumDetailScreenState.asStateFlow()

    init {
        collectPlaylist()
    }

    fun initialize(context: Context, id: String) = viewModelScope.launch(Dispatchers.IO) {
        val mediaId = MediaId.AllSongs
        val musicList = getMusicList(context, mediaId)
        val album = musicList
            .filter { it.albumId == id }
            .run {
                Album(
                    id = id,
                    name = this.firstOrNull()?.album.defaultEmpty(),
                    artist = this.firstOrNull()?.artist.defaultEmpty(),
                    artistId = this.firstOrNull()?.artistId.defaultEmpty(),
                    imageUrl = this.firstOrNull()?.imageUrl.defaultEmpty(),
                    songs = this.sortedBy { it.trackNumber }
                )
            }

        _musicAlbumDetailScreenState.update {
            it.copy(album = album)
        }
    }

    fun dispatch(event: MusicAlbumDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicAlbumDetailScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back)
        }
    }

    fun onSongItemEvent(event: SongItemEvent) = viewModelScope.launch {
        songItemEventUseCase.dispatch(event)
    }

    private fun collectPlaylist() {
        flow {
            emit(playlistUseCase.getAllPlaylist())
        }.onEach { result ->
            result.onSuccess { playlists ->
                _musicAlbumDetailScreenState.update {
                    it.copy(
                        playlists = playlists
                    )
                }
            }
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }
}