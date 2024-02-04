package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail.model.MusicListDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.detail.model.MusicListDetailScreenState
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListDetailViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    private val playlistUseCase: PlaylistUseCase,
    musicStateHolder: MusicStateHolder,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playbackEventUseCase) {

    private val _musicListDetailScreenState = MutableStateFlow(MusicListDetailScreenState.default)
    val musicListDetailScreenState = _musicListDetailScreenState.asStateFlow()

    init {
        collectPlaylist()
        collectMusicList()

    }

    fun initMusicListType(musicListType: MusicListType) {
        musicListUseCase.setMusicListType(musicListType)
        loadMusicList()
    }

    fun dispatch(event: MusicListDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicListDetailScreenEvent.OnMusicListTypeChanged -> onMusicListTypeChanged(event.musicListType)
        }
    }


    private fun onMusicListTypeChanged(musicListType: MusicListType) {
        musicListUseCase.setMusicListType(musicListType)
    }
    private fun collectPlaylist() = viewModelScope.launch {
        playlistUseCase.allPlaylist().collectLatest { playlists ->
            _musicListDetailScreenState.update {
                it.copy(
                    playlists = playlists
                )
            }
        }
    }

    private fun collectMusicList() = viewModelScope.launch {
        combine(
            musicListUseCase.localSongList,
            musicListUseCase.assetSongList,
            musicListUseCase.streamingSongList,
            musicListUseCase.musicListType,
        ) { localSongList, assetSongList, streamingSongList, musicListType ->
            Pair(listOf(localSongList, assetSongList, streamingSongList), musicListType)
        }.collect { (dataSet, musicListType) ->
            val localSongList = dataSet[0]
            val assetSongList = dataSet[1]
            val streamingSongList = dataSet[2]
            val songList = when(musicListType) {
                MusicListType.All -> localSongList + assetSongList + streamingSongList
                MusicListType.Asset -> assetSongList
                MusicListType.Local -> localSongList
                MusicListType.Streaming -> streamingSongList
            }

            _musicListDetailScreenState.update {
                it.copy(
                    songList = songList,
                    musicListType = musicListType
                )
            }
        }
    }

    private fun loadMusicList() = viewModelScope.launch {
        musicListUseCase.initialize()
    }
}