package com.jooheon.clean_architecture.features.musicplayer.presentation.album

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicAlbumScreenViewModel @Inject constructor(
    musicControllerUsecase: MusicControllerUsecase,
    private val musicListUseCase: MusicListUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG = MusicAlbumScreenViewModel::class.java.simpleName

    private val _musicAlbumScreenState = MutableStateFlow(MusicAlbumScreenState.default)
    val musicAlbumScreenState = _musicAlbumScreenState.asStateFlow()

    private val _navigateToDetailScreen = Channel<Album>()
    val navigateToDetailScreen = _navigateToDetailScreen.receiveAsFlow()

    init {
        collectMusicList()
        loadData()
    }

    fun dispatch(event: MusicAlbumScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicAlbumScreenEvent.OnAlbumItemClick -> _navigateToDetailScreen.send(event.album)
        }
    }
    fun loadData() = viewModelScope.launch {
//        val playlistType = musicControllerUsecase.musicState.value.playlistType
//        musicControllerUsecase.loadPlaylist(playlistType)
    }

    private suspend fun updateAlbums(musicList: List<Song>) = withContext(Dispatchers.IO) {
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

        _musicAlbumScreenState.update {
            it.copy(
                albums = groupByAlbum
            )
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

            val songList = when(musicListType) {
                MusicListType.All -> localSongList + streamingSongList
                MusicListType.Local -> localSongList
                MusicListType.Streaming -> streamingSongList
            }
            updateAlbums(songList)
        }
    }
}