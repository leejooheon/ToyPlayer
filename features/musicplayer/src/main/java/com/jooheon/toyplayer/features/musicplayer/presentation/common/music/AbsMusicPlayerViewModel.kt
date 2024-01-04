package com.jooheon.toyplayer.features.musicplayer.presentation.common.music

import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.common.PlayerController
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicservice.ext.isPlaying
import com.jooheon.toyplayer.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.MusicStateHolder
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class AbsMusicPlayerViewModel (
    private val controller: PlayerController,
    private val musicControllerUsecase: MusicControllerUseCase,
    private val musicStateHolder: MusicStateHolder,
): BaseViewModel() {
    override val TAG = AbsMusicPlayerViewModel::class.java.simpleName

    private val _musicPlayerState = MutableStateFlow(MusicPlayerState.default)
    val musicPlayerState = _musicPlayerState.asStateFlow()

    protected val _navigateToPlayingQueueScreen = Channel<Playlist>()
    val navigateToPlayingQueueScreen = _navigateToPlayingQueueScreen.receiveAsFlow()

    init {
        collectMusicState()
    }

    fun dispatch(event: MusicPlayerEvent) = viewModelScope.launch {
        when(event) {
            is MusicPlayerEvent.OnPlayingQueueClick -> onPlayingQueueClick()
            is MusicPlayerEvent.OnPlayPauseClick -> onPlayPauseButtonClicked(event.song)
            is MusicPlayerEvent.OnSnapTo -> snapTo(event.duration)
            is MusicPlayerEvent.OnNextClick -> onNextClicked()
            is MusicPlayerEvent.OnPreviousClick -> onPreviousClicked()
            is MusicPlayerEvent.OnPause -> { /** Nothing **/}
            is MusicPlayerEvent.OnRepeatClick -> onRepeatClicked()
            is MusicPlayerEvent.OnShuffleClick -> onShuffleClicked()
            is MusicPlayerEvent.OnSongClick -> onSongClick(event.song)
            is MusicPlayerEvent.OnEnqueue -> onEnqueue(event.songs, event.shuffle, event.playWhenReady)
            is MusicPlayerEvent.OnDeleteClick -> onDeleteClick(event.song)
        }
    }

    private fun onEnqueue(
        songs: List<Song>,
        shuffle: Boolean,
        playWhenReady: Boolean
    ) = executeAfterPrepare { player ->
        musicControllerUsecase.enqueue(
            player = player,
            songs = songs,
            addNext = false,
            playWhenReady = playWhenReady,
        )
    }

    private fun onSongClick(song: Song) = executeAfterPrepare { player ->
        musicControllerUsecase.onPlay(player, song)
    }

    private fun onNextClicked() = executeAfterPrepare { player ->
        musicControllerUsecase.onNext(player)
    }

    private fun onPreviousClicked() = executeAfterPrepare { player ->
        musicControllerUsecase.onPrevious(player)
    }
    private fun onDeleteClick(song: Song) = executeAfterPrepare { player ->
        musicControllerUsecase.onDeleteAtPlayingQueue(player, listOf(song))
    }

    private suspend fun onPlayingQueueClick() {
        _navigateToPlayingQueueScreen.send(
            Playlist.playingQueuePlaylist.copy(
                songs = musicPlayerState.value.playingQueue
            )
        )
    }

    private fun onPlayPauseButtonClicked(song: Song) = executeAfterPrepare { player ->
        if(musicPlayerState.value.musicState.playbackState.isPlaying) {
            musicControllerUsecase.onPause(player)
        } else {
            musicControllerUsecase.onPlay(player, song)
        }
    }

    private fun onShuffleClicked() = executeAfterPrepare { player ->
        musicControllerUsecase.onShuffleButtonPressed(player)
    }

    private fun onRepeatClicked() = executeAfterPrepare { player ->
        musicControllerUsecase.onRepeatButtonPressed(player)
    }

    private fun snapTo(duration: Long) = executeAfterPrepare { player ->
        musicControllerUsecase.snapTo(player, duration)
    }
    private fun collectMusicState() = viewModelScope.launch {
        musicStateHolder.musicState.collectLatest { musicState ->
            _musicPlayerState.update {
                it.copy(
                    musicState = musicState
                )
            }
        }
    }

    private inline fun executeAfterPrepare(crossinline action: suspend (MediaController) -> Unit) {
        viewModelScope.launch {
            val controller = controller.awaitConnect() ?: return@launch
            action(controller)
        }
    }
}