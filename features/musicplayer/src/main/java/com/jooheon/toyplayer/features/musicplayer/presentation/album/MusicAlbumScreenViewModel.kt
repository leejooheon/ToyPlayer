package com.jooheon.toyplayer.features.musicplayer.presentation.album

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.core.navigation.ScreenNavigation
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.album.model.MusicAlbumScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.album.model.MusicAlbumScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicAlbumScreenViewModel @Inject constructor(
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG = MusicAlbumScreenViewModel::class.java.simpleName

    private val _musicAlbumScreenState = MutableStateFlow(MusicAlbumScreenState.default)
    val musicAlbumScreenState = _musicAlbumScreenState.asStateFlow()

    private val _sortType = MutableStateFlow(AlbumSortType.ArtistName)
    val sortType = _sortType.asStateFlow()

    enum class AlbumSortType {
        AlbumName, ArtistName
    }

    fun dispatch(event: MusicAlbumScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicAlbumScreenEvent.OnAlbumItemClick -> {
                val screen = ScreenNavigation.Music.AlbumDetail(event.album.id)
                _navigateTo.send(screen)
            }
            is MusicAlbumScreenEvent.OnSortTypeChanged -> _sortType.tryEmit(event.type)
        }
    }

    fun loadData(
        context: Context,
        albumSortType: AlbumSortType,
    ) = viewModelScope.launch {
        updateAlbums(context, albumSortType)
    }

    private suspend fun updateAlbums(
        context: Context,
        albumSortType: AlbumSortType,
    ) = withContext(Dispatchers.IO) {

        val mediaId = MediaId.AllSongs
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
        }.sortedBy {
            when(albumSortType) {
                AlbumSortType.AlbumName -> it.name
                AlbumSortType.ArtistName -> it.artist
            }
        }

        _musicAlbumScreenState.update {
            it.copy(
                albums = groupByAlbum
            )
        }
    }
}