package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.album.model.MusicAlbumScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
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
class MusicAlbumScreenViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    musicStateHolder: MusicStateHolder,
    playbackEventUseCase: PlaybackEventUseCase
): AbsMusicPlayerViewModel(musicStateHolder, playbackEventUseCase) {
    override val TAG = MusicAlbumScreenViewModel::class.java.simpleName

    private val _musicAlbumScreenState = MutableStateFlow(MusicAlbumScreenState.default)
    val musicAlbumScreenState = _musicAlbumScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Album>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    private val sortType = MutableStateFlow(true)
    private var songList: List<Song> = emptyList()

    init {
        collectMusicList()
        collectSortType()
        loadData()
    }

    fun dispatch(event: MusicAlbumScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicAlbumScreenEvent.OnAlbumItemClick -> _navigateToDetailScreen.send(event.album)
            is MusicAlbumScreenEvent.OnSortByAlbumName -> sortType.tryEmit(true)
            is MusicAlbumScreenEvent.OnSortByArtistName -> sortType.tryEmit(false)
        }
    }

    fun loadData() = viewModelScope.launch {
//        val playlistType = musicControllerUsecase.musicState.value.playlistType
//        musicControllerUsecase.loadPlaylist(playlistType)
    }

    private suspend fun updateAlbums() = withContext(Dispatchers.IO) {
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
        }.sortedBy {
            if(sortType.value) it.name
            else it.artist
        }

        _musicAlbumScreenState.update {
            it.copy(
                albums = groupByAlbum
            )
        }
    }

    private fun collectSortType() = viewModelScope.launch {
        sortType.collectLatest {
            updateAlbums()
        }
    }

    private fun collectMusicList() = viewModelScope.launch {
        combine(
            musicListUseCase.localSongList,
            musicListUseCase.assetSongList,
            musicListUseCase.streamingSongList,
            musicListUseCase.musicListType,
        ) { localSongList, assetSongList, streamingSongList, musicListType ->
            Triple(localSongList + assetSongList, streamingSongList, musicListType)
        }.collect { (localSongList, streamingSongList, musicListType) ->
            songList = when(musicListType) {
                MusicListType.All -> localSongList + streamingSongList
                MusicListType.Local -> localSongList
                MusicListType.Streaming -> streamingSongList
            }
            updateAlbums()
        }
    }
}