package com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.artist.detail.model.MusicArtistDetailScreenState
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicArtistDetailScreenViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    private val playlistUseCase: PlaylistUseCase,
    private val songItemEventUseCase: SongItemEventUseCase,
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG = MusicArtistDetailScreenViewModel::class.java.simpleName

    private val _musicArtistDetailScreenState = MutableStateFlow(MusicArtistDetailScreenState.default)
    val musicArtistDetailScreenState = _musicArtistDetailScreenState.asStateFlow()

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

        val albums = musicList
            .filter { it.artistId == id }
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
            .firstOrNull { it.artistId == id }
            .run {
                Artist(
                    id = id,
                    name = this?.artist.defaultEmpty(),
                    albums = albums
                )
            }

        _musicArtistDetailScreenState.update {
            it.copy(artist = artist)
        }
    }
    fun dispatch(event: MusicArtistDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicArtistDetailScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back)
            is MusicArtistDetailScreenEvent.OnAlbumClick -> {
                val screen = ScreenNavigation.Music.AlbumDetail(event.album.id)
                _navigateTo.send(screen)
            }
        }
    }
    fun onSongItemEvent(event: SongItemEvent) = viewModelScope.launch {
        songItemEventUseCase.dispatch(event)
    }

    private fun collectPlaylistState() = viewModelScope.launch{
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicArtistDetailScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }
}
