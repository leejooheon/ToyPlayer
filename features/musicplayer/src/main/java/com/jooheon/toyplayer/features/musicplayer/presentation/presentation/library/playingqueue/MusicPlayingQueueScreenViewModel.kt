package com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue

import androidx.lifecycle.viewModelScope
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.mediaitem.model.MusicMediaItemEventUseCase
import com.jooheon.toyplayer.features.common.compose.ScreenNavigation
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.AbsMusicPlayerViewModel
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model.MusicPlayingQueueScreenEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.presentation.library.playingqueue.model.MusicPlayingQueueScreenState
import com.jooheon.toyplayer.features.musicservice.ext.isPlaying
import com.jooheon.toyplayer.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MusicPlayingQueueScreenViewModel @Inject constructor(
    private val musicControllerUsecase: MusicControllerUseCase,
    private val musicMediaItemEventUseCase: MusicMediaItemEventUseCase,
    private val musicStateHolder: MusicStateHolder,
    private val playingQueueUseCase: PlayingQueueUseCase,
): AbsMusicPlayerViewModel(musicControllerUsecase, musicStateHolder) {
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
            is MusicPlayingQueueScreenEvent.OnDeleteClick -> musicControllerUsecase.onDeleteAtPlayingQueue(listOf(event.song))
            is MusicPlayingQueueScreenEvent.OnActionPlayAll -> onActionPlayAll(event.shuffle)
        }
    }

    fun onMusicMediaItemEvent(event: MusicMediaItemEvent) = viewModelScope.launch {
        musicMediaItemEventUseCase.dispatch(event)
    }

    private fun onActionPlayAll(shuffle: Boolean) {
        Timber.d("OnActionPlayAll: $shuffle")
        val currentPlayingQueue = musicPlayingQueueScreenState.value.playlist.songs

        val songs = if(shuffle) currentPlayingQueue.shuffled()
        else currentPlayingQueue

        val isPlaying = musicPlayerState.value.musicState.playbackState.isPlaying
        if(shuffle) {
            musicControllerUsecase.shuffle(
                playWhenReady = isPlaying
            )
        } else {
            musicControllerUsecase.enqueue(
                songs = songs,
                addNext = false,
                playWhenReady = true
            )
        }
    }

    private fun collectPlayingQueue() = viewModelScope.launch {
        playingQueueUseCase.playingQueue().onEach {
            if(it !is Resource.Success) return@onEach

            val playingQueue = it.value
            _musicPlayingQueueScreenState.update {
                it.copy(
                    playlist = Playlist.playingQueuePlaylist.copy(
                        songs = playingQueue
                    )
                )
            }
        }.launchIn(this)
    }
}