package com.jooheon.clean_architecture.features.musicplayer.presentation.song

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.toyproject.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicSongScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.song.model.MusicSongScreenState
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSongScreenViewModel @Inject constructor(
    musicControllerUsecase: MusicControllerUsecase,
    private val musicListUseCase: MusicListUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    private val playlistUseCase: PlaylistUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG: String = MusicSongScreenViewModel::class.java.simpleName

    private val _musicSongScreenState = MutableStateFlow(MusicSongScreenState.default)
    val musicPlayerScreenState = _musicSongScreenState.asStateFlow()

    init {
        collectPlaylist()
        collectMusicList()

        musicListUseCase.loadSongList(MusicUtil.localMusicStorageUri().toString())
    }

    fun dispatch(event: MusicSongScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicSongScreenEvent.OnMusicListTypeChanged -> onMusicListTypeChanged(event.musicListType)
            is MusicSongScreenEvent.OnItemViewTypeChanged -> {}
        }
    }

    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
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

            _musicSongScreenState.update {
                it.copy(
                    songList = songList,
                    musicListType = musicListType
                )
            }
        }
    }
}