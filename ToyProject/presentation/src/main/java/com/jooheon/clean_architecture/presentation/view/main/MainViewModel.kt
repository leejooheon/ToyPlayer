package com.jooheon.clean_architecture.presentation.view.main

import android.media.MediaMetadata
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerDataSource
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicScreenState
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicState
import com.jooheon.clean_architecture.presentation.service.music.extensions.getMusicState
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicUseCase: MusicUseCase,
    private val musicPlayerRemote: MusicPlayerRemote
): BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    private val _uiState = mutableStateOf(MusicScreenState())
    val uiState: State<MusicScreenState> = _uiState

    private val _lastSearchedOwner = mutableStateOf("")
    val lastSearchedOwner = _lastSearchedOwner

    private var _isDoubleBackPressed = mutableStateOf(true) // FIXME: 2번연속했을떄 안되넴
    val isDoubleBackPressed = _isDoubleBackPressed

    private val _floatingActionClicked = Channel<Unit>()
    val floatingActionClicked = _floatingActionClicked.receiveAsFlow()

    private val _floatingButtonState = mutableStateOf(false)
    val floatingButtonState = _floatingButtonState

    init {
        collectTimePassed()
        collectCurrentSong()
        collectPlaybackState()
        collectAllSongs()
    }

    private fun collectTimePassed() = viewModelScope.launch {
        musicPlayerRemote.timePassed.collectLatest {
            Log.d(TAG, "collectTimePassed - $it")
        }
    }

    private fun collectCurrentSong() = viewModelScope.launch {
        musicPlayerRemote.currentSong.collectLatest {
            val mediaMeta = it ?: return@collectLatest
            (mediaMeta.mediaMetadata as? MediaMetadata) ?.let {
                Log.d("JH", "r: albumId - ${it.getString(MediaMetadata.METADATA_KEY_ARTIST)}")
                Log.d("JH", "r: albumName - ${it.getString(MediaMetadata.METADATA_KEY_AUTHOR)}")

                val song = MusicUtil.parseSongFromMediaMetadata(it)
                _uiState.value = uiState.value.copy(currentPlayingMusic = song)
            } ?: run {
                Log.d(TAG, "collectCurrentSong - null")
            }
        }
    }

    private fun collectPlaybackState() = viewModelScope.launch {
        musicPlayerRemote.playbackState.collectLatest {
            val musicState = it?.getMusicState() ?: MusicState.NONE
            Log.d(TAG, "collectPlaybackState - ${musicState}")
            _uiState.value = uiState.value.copy(musicState = musicState)
        }
    }

    private fun collectAllSongs() = viewModelScope.launch {
        musicPlayerRemote.allSongs.collectLatest {
            val allSongs = it ?: return@collectLatest
            Log.d(TAG, "collectAllSongs - ${allSongs.size}")
            _uiState.value = uiState.value.copy(songList = allSongs)
        }
    }

    fun onPlayPauseButtonPressed(song: Entity.Song) = viewModelScope.launch(Dispatchers.Main) {
        musicPlayerRemote.playPause(song.id.toString(), true)
    }

    fun onMusicBottomBarPressed(song: Entity.Song) = viewModelScope.launch(Dispatchers.Main) {
//        _navigateToMusicScreen.emit(true)
        Log.d(TAG, "onMusicBottomBarPressed")
    }

    fun onNavigationClicked() {
        Log.d(TAG, "onNavigationClicked")
    }

    fun onFavoriteClicked() {
        Log.d(TAG, "onFavoriteClicked")
    }

    fun onSearchClicked() {
        Log.d(TAG, "onSearchClicked")
    }

    fun onSettingClicked() {
        Log.d(TAG, "onSettingClicked")
    }

    fun onFloatingButtonClicked() {
        _floatingButtonState.value = !(_floatingButtonState.value)
        viewModelScope.launch {
            _floatingActionClicked.send(Unit)
        }
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _isDoubleBackPressed.value = false
            delay(2000)
            _isDoubleBackPressed.value = true
        }
    }
}