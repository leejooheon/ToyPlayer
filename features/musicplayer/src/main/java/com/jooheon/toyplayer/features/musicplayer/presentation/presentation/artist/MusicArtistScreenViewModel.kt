package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.Artist
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.artist.model.MusicArtistScreenState
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicArtistScreenViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG = MusicArtistScreenViewModel::class.java.simpleName

    private val _musicArtistScreenState = MutableStateFlow(MusicArtistScreenState.default)
    val musicArtistScreenState = _musicArtistScreenState.asStateFlow()

    private val _musicListType = MutableStateFlow(musicListUseCase.getMusicListType())
    val musicListType = _musicListType.asStateFlow()

    private val _sortType = MutableStateFlow(ArtistSortType.ArtistName)
    val sortType = _sortType.asStateFlow()

    enum class ArtistSortType {
        ArtistName, NumberOfSong, NumberOfAlbum
    }

    init {
        collectMusicList()
    }

    fun dispatch(event: MusicArtistScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicArtistScreenEvent.OnArtistItemClick -> {
                val screen = ScreenNavigation.Music.ArtistDetail(event.artist.id)
                _navigateTo.send(screen)
            }
            is MusicArtistScreenEvent.OnSortTypeChanged -> _sortType.tryEmit(event.type)
        }
    }

    fun loadData(
        context: Context,
        artistSortType: ArtistSortType,
        musicListType: MusicListType,
    ) = viewModelScope.launch {
        updateArtists(context, artistSortType, musicListType)
    }

    private suspend fun updateArtists(
        context: Context,
        artistSortType: ArtistSortType,
        musicListType: MusicListType,
    ) = withContext(Dispatchers.IO) {
        val mediaId = when(musicListType) {
            MusicListType.All -> MediaId.AllSongs
            MusicListType.Local -> MediaId.LocalSongs
            MusicListType.Streaming -> MediaId.StreamSongs
            MusicListType.Asset -> MediaId.AssetSongs
        }

        val musicList = getMusicList(context, mediaId)

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
            Artist(
                id = artistId,
                name = songs.firstOrNull()?.artist.defaultEmpty(),
                albums = groupByAlbum[artistId] ?: emptyList()
            )
        }

        val sorted = when(artistSortType) {
            ArtistSortType.ArtistName -> groupByArtist.sortedBy { it.name }
            ArtistSortType.NumberOfSong -> groupByArtist.sortedByDescending { it.albums.map { it.songs.size }.sum() }
            ArtistSortType.NumberOfAlbum -> groupByArtist.sortedByDescending { it.albums.size }
        }

        _musicArtistScreenState.update {
            it.copy(artists = sorted)
        }
    }

    private fun collectMusicList() = viewModelScope.launch {
        musicListUseCase.musicListType.collectLatest {
            _musicListType.emit(it)
        }
    }
}