package com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue

import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.toyproject.features.common.compose.ScreenNavigation
import com.jooheon.clean_architecture.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue.model.MusicPlayingQueueScreenEvent
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playingqueue.model.MusicPlayingQueueScreenState
import com.jooheon.clean_architecture.features.musicplayer.presentation.library.playlist.detail.model.MusicPlaylistDetailScreenEvent
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MusicPlayingQueueScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUsecase
): AbsMusicPlayerViewModel(musicControllerUsecase) {
    override val TAG = MusicPlayingQueueScreenViewModel::class.java.simpleName

    private val _musicPlayingQueueScreenState = MutableStateFlow(MusicPlayingQueueScreenState.default)
    val musicPlayingQueueScreenState = _musicPlayingQueueScreenState.asStateFlow()

    private val _navigateTo = Channel<String>()
    val navigateTo = _navigateTo.receiveAsFlow()

    init {
        collectPlayingQueue()
    }

    fun dispatch(event: MusicPlayingQueueScreenEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayingQueueScreenEvent.OnBackClick -> _navigateTo.send(ScreenNavigation.Back.route)
            is MusicPlayingQueueScreenEvent.OnSongClick ->  musicControllerUsecase.onPlay(event.song)
            is MusicPlayingQueueScreenEvent.OnActionPlayAll -> {
                Timber.d("OnActionPlayAll: ${event.shuffle}")
                val currentPlayingQueue = musicPlayingQueueScreenState.value.playlist.songs

                val songs = if(event.shuffle) currentPlayingQueue.shuffled()
                            else currentPlayingQueue

                musicControllerUsecase.onOpenQueue(
                    songs = songs,
                    addToPlayingQueue = false,
                    autoPlay = true
                )
            }
        }
    }

    private fun collectPlayingQueue() = viewModelScope.launch {
        musicControllerUsecase.musicState.collectLatest { musicState ->
            _musicPlayingQueueScreenState.update {
                it.copy(
                    playlist = Playlist.playingQueuePlaylist.copy(
                        songs = musicState.playingQueue
                    )
                )
            }
        }
    }
}