package com.jooheon.toyplayer.features.musicplayer.presentation.song.detail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase.PlaybackEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.model.MusicListDetailScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.song.detail.model.MusicListDetailScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicListDetailViewModel @Inject constructor(
    private val musicListUseCase: MusicListUseCase,
    private val playlistUseCase: PlaylistUseCase,
    musicStateHolder: MusicStateHolder,
    playerController: PlayerController,
    playbackEventUseCase: PlaybackEventUseCase,
): AbsMusicPlayerViewModel(musicStateHolder, playerController, playbackEventUseCase) {

    private val _musicListDetailScreenState = MutableStateFlow(MusicListDetailScreenState.default)
    val musicListDetailScreenState = _musicListDetailScreenState.asStateFlow()

    private val _musicListType = MutableStateFlow(musicListUseCase.getMusicListType())
    val musicListType = _musicListType.asStateFlow()

    init {
        collectPlaylist()
        collectMusicList()

    }

    fun initMusicListType(musicListType: MusicListType) {
        musicListUseCase.setMusicListType(musicListType)
    }

    fun dispatch(event: MusicListDetailScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicListDetailScreenEvent.OnMusicListTypeChanged -> onMusicListTypeChanged(event.musicListType)
            is MusicListDetailScreenEvent.OnRefresh -> loadMusicList(event.context, event.musicListType)
        }
    }

    private fun onMusicListTypeChanged(musicListType: MusicListType) {
        musicListUseCase.setMusicListType(musicListType)
    }

    private suspend fun loadMusicList(context: Context, musicListType: MusicListType) {
        val mediaId = when(musicListType) {
            MusicListType.All -> MediaId.AllSongs
            MusicListType.Local -> MediaId.LocalSongs
            MusicListType.Streaming -> MediaId.StreamSongs
            MusicListType.Asset -> MediaId.AssetSongs
        }

        val songList = getMusicList(context, mediaId)

        _musicListDetailScreenState.update {
            it.copy(
                songList = songList,
                musicListType = musicListType
            )
        }
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
//            _musicListDetailScreenState.update {
//                it.copy(
//                    songList = songList,
//                    musicListType = musicListType
//                )
//            }
//        }
//    }
}