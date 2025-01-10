package com.jooheon.toyplayer.features.musicplayer.presentation.album.detail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicAlbumDetailScreenViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
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
        collectPlaylistState()
    }

    fun initialize(context: Context, id: String) = viewModelScope.launch(Dispatchers.IO) {
        val type = musicListUseCase.getMusicListType()
        val mediaId = when(type) {
            MusicListType.All -> MediaId.AllSongs
            MusicListType.Local -> MediaId.LocalSongs
            MusicListType.Streaming -> MediaId.StreamSongs
            MusicListType.Asset -> MediaId.AssetSongs
        }
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

    private fun collectPlaylistState() = viewModelScope.launch {
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicAlbumDetailScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}