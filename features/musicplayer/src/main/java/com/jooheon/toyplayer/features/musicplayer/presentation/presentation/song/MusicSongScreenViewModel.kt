package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.SongItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.SongItemEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSongScreenViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    private val songItemEventUseCase: SongItemEventUseCase,
    private val playlistUseCase: PlaylistUseCase,
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {
    override val TAG: String = MusicSongScreenViewModel::class.java.simpleName

    private val _musicSongScreenState = MutableStateFlow(MusicSongScreenState.default)
    val musicPlayerScreenState = _musicSongScreenState.asStateFlow()

    private val _musicListType = MutableStateFlow(musicListUseCase.getMusicListType())
    val musicListType = _musicListType.asStateFlow()

    init {
        collectPlaylist()
        collectMusicList()

        loadMusicList()
    }

    fun dispatch(event: MusicSongScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicSongScreenEvent.OnMusicListTypeChanged -> onMusicListTypeChanged(event.musicListType)
            is MusicSongScreenEvent.OnRefresh -> reload(event.context, event.musicListType)
            is MusicSongScreenEvent.OnMusicComponentClick -> {
                val screen = ScreenNavigation.Music.MusicListDetail(event.musicListType.ordinal)
                _navigateTo.send(screen)
            }
        }
    }

    fun onSongItemEvent(event: SongItemEvent) = viewModelScope.launch {
        songItemEventUseCase.dispatch(event)
    }

    private suspend fun reload(context: Context, musicListType: MusicListType) {
        val mediaId = when(musicListType) {
            MusicListType.All -> MediaId.AllSongs
            MusicListType.Local -> MediaId.LocalSongs
            MusicListType.Streaming -> MediaId.StreamSongs
            MusicListType.Asset -> MediaId.AssetSongs
        }

        val songList = getMusicList(context, mediaId)

        _musicSongScreenState.update {
            it.copy(
                songList = songList,
                musicListType = musicListType
            )
        }
    }

    private fun onMusicListTypeChanged(musicListType: MusicListType) {
        musicListUseCase.setMusicListType(musicListType)
    }
    private fun collectPlaylist() = viewModelScope.launch {
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicSongScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }

    private fun collectMusicList() = viewModelScope.launch {
        musicListUseCase.musicListType.collectLatest {
            _musicListType.emit(it)
        }
    }

//    private fun collectMusicList() = viewModelScope.launch {
//        combine(
//            musicListUseCase.localSongList,
//            musicListUseCase.assetSongList,
//            musicListUseCase.streamingSongList,
//            musicListUseCase.musicListType,
//        ) { localSongList, assetSongList, streamingSongList, musicListType ->
//            Pair(listOf(localSongList, assetSongList, streamingSongList), musicListType)
//        }.collect { (dataSet, musicListType) ->
//            val localSongList = dataSet[0]
//            val assetSongList = dataSet[1]
//            val streamingSongList = dataSet[2]
//            val songList = when(musicListType) {
//                MusicListType.All -> localSongList + assetSongList + streamingSongList
//                MusicListType.Asset -> assetSongList
//                MusicListType.Local -> localSongList
//                MusicListType.Streaming -> streamingSongList
//            }
//
//            _musicSongScreenState.update {
//                it.copy(
//                    songList = songList,
//                    musicListType = musicListType
//                )
//            }
//        }
//    }

    private fun loadMusicList() = viewModelScope.launch {
        musicListUseCase.setMusicListType(MusicListType.All)
    }
}