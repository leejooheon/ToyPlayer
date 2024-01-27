package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.song.model.MusicSongScreenState
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
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
    private val musicListUseCase: MusicListUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    private val playlistUseCase: PlaylistUseCase,
    playerController: PlayerController,
    musicStateHolder: MusicStateHolder,
): AbsMusicPlayerViewModel(playerController, musicStateHolder) {
    override val TAG: String = MusicSongScreenViewModel::class.java.simpleName

    private val _musicSongScreenState = MutableStateFlow(MusicSongScreenState.default)
    val musicPlayerScreenState = _musicSongScreenState.asStateFlow()

    init {
        collectPlaylist()
        collectMusicList()

        loadMusicList()
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
            musicListUseCase.assetSongList,
            musicListUseCase.streamingSongList,
            musicListUseCase.musicListType,
        ) { localSongList, assetSongList, streamingSongList, musicListType ->
            Triple(localSongList + assetSongList, streamingSongList, musicListType)
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

    private fun loadMusicList() = viewModelScope.launch {
        musicListUseCase.initialize()
    }
}