package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase

import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController

class PlaybackEventUseCase(
    private val playerController: PlayerController,
    private val musicStateHolder: MusicStateHolder,
) {
    fun dispatch(event: MusicPlayerEvent) {
        when(event) {
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
            else -> { /** Nothing **/ }
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

    private fun onPlayPauseButtonClicked(song: Song) {
        if(musicStateHolder.isPlaying.value || musicStateHolder.playWhenReady.value) {
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

}