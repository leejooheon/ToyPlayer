package com.jooheon.clean_architecture.features.musicplayer.presentation.artist

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.Artist
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.artist.model.MusicArtistScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicArtistScreenViewModel @Inject constructor(
    musicControllerUsecase: MusicControllerUsecase,
    private val musicListUseCase: MusicListUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG = MusicArtistScreenViewModel::class.java.simpleName

    private val _musicArtistScreenState = MutableStateFlow(MusicArtistScreenState.default)
    val musicArtistScreenState = _musicArtistScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Artist>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    private val sortType = MutableStateFlow(ArtistSortType.ArtistName)
    private var songList: List<Song> = emptyList()

    private enum class ArtistSortType {
        ArtistName, NumberOfSong, NumberOfAlbum
    }
    init {
        collectMusicList()
        collectSortType()
        loadData()
    }

    fun dispatch(event: MusicArtistScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicArtistScreenEvent.OnArtistItemClick -> _navigateToDetailScreen.send(event.artist)
            is MusicArtistScreenEvent.OnSortByArtistName -> sortType.tryEmit(ArtistSortType.ArtistName)
            is MusicArtistScreenEvent.OnSortByNumberOfSong -> sortType.tryEmit(ArtistSortType.NumberOfSong)
            is MusicArtistScreenEvent.OnSortByNumberOfAlbum ->sortType.tryEmit(ArtistSortType.NumberOfAlbum)
        }
    }

    fun loadData() = viewModelScope.launch {
        // TODO
    }

    private suspend fun updateArtists() = withContext(Dispatchers.IO) {
        val groupByAlbum = songList.groupBy {
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

        val groupByArtist = songList.groupBy {
            it.artistId
        }.map { (artistId, songs) ->
            Artist(
                id = artistId,
                name = songs.firstOrNull()?.artist.defaultEmpty(),
                albums = groupByAlbum[artistId] ?: emptyList()
            )
        }

        val sorted = when(sortType.value) {
            ArtistSortType.ArtistName -> groupByArtist.sortedBy { it.name }
            ArtistSortType.NumberOfSong -> groupByArtist.sortedByDescending { it.albums.map { it.songs.size }.sum() }
            ArtistSortType.NumberOfAlbum -> groupByArtist.sortedByDescending { it.albums.size }
        }

        _musicArtistScreenState.update {
            it.copy(artists = sorted)
        }
    }

    private fun collectSortType() = viewModelScope.launch {
        sortType.collectLatest {
            updateArtists()
        }
    }
    private fun collectMusicList() = viewModelScope.launch {
        combine(
            musicListUseCase.localSongList,
            musicListUseCase.streamingSongList,
            musicListUseCase.musicListType,
        ) { localSongList, streamingSongList, musicListType ->
            Triple(localSongList, streamingSongList, musicListType)
        }.collect { (localSongList, streamingSongList, musicListType) ->

            songList = when(musicListType) {
                MusicListType.All -> localSongList + streamingSongList
                MusicListType.Local -> localSongList
                MusicListType.Streaming -> streamingSongList
            }

            updateArtists()
        }
    }
}