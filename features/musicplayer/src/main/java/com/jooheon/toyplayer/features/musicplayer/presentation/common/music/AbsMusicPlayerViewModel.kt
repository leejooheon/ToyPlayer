package com.jooheon.toyplayer.features.musicplayer.presentation.common.music

import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicservice.player.PlayerController
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerState
import com.jooheon.toyplayer.features.musicservice.ext.isPlaying
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.common.base.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class AbsMusicPlayerViewModel (
    private val playerController: PlayerController,
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
    ) {
        val shuffled = if(shuffle) songs.shuffled() else songs

        playerController.enqueue(
            songs = shuffled,
            addNext = false,
            playWhenReady = playWhenReady,
        )
    }

    private fun onSongClick(song: Song) {
        playerController.play(song)
    }

    private fun onNextClicked() {
        playerController.seekToNext()
    }

    private fun onPreviousClicked() {
        playerController.seekToPrevious()
    }
    private fun onDeleteClick(song: Song) {
        playerController.onDeleteAtPlayingQueue(listOf(song))
    }

    private suspend fun onPlayingQueueClick() {
        _navigateToPlayingQueueScreen.send(
            Playlist.playingQueuePlaylist.copy(
                songs = musicPlayerState.value.playingQueue
            )
        )
    }

    private fun onPlayPauseButtonClicked(song: Song) {
        if(musicPlayerState.value.musicState.playbackState.isPlaying) {
            playerController.pause()
        } else {
            playerController.play(song)
        }
    }

    private fun onShuffleClicked() {
        playerController.shuffle()
    }

    private fun onRepeatClicked() {
        playerController.repeat()
    }

    private fun snapTo(duration: Long) {
        playerController.snapTo(duration)
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
}