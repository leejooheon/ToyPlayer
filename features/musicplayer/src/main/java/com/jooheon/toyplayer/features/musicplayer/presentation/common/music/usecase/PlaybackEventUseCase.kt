package com.jooheon.toyplayer.features.musicplayer.presentation.common.music.usecase

import android.content.Context
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.musicplayer.presentation.common.music.model.MusicPlayerEvent
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.player.PlayerController

class PlaybackEventUseCase(
    private val musicStateHolder: MusicStateHolder,
) {
    fun dispatch(playerController: PlayerController, event: MusicPlayerEvent) {
        when(event) {
            is MusicPlayerEvent.OnPlayPauseClick -> onPlayPauseButtonClicked(playerController, event.song)
            is MusicPlayerEvent.OnSnapTo -> snapTo(playerController, event.duration)
            is MusicPlayerEvent.OnNextClick -> onNextClicked(playerController)
            is MusicPlayerEvent.OnPreviousClick -> onPreviousClicked(playerController)
            is MusicPlayerEvent.OnPause -> { /** Nothing **/}
            is MusicPlayerEvent.OnRepeatClick -> onRepeatClicked(playerController)
            is MusicPlayerEvent.OnShuffleClick -> onShuffleClicked(playerController)
            is MusicPlayerEvent.OnSongClick -> onSongClick(playerController, event.song)
            is MusicPlayerEvent.OnEnqueue -> onEnqueue(playerController, event.songs, event.shuffle, event.playWhenReady)
            is MusicPlayerEvent.OnDeleteClick -> onDeleteClick(playerController, event.song)
            else -> { /** Nothing **/ }
        }
    }
    suspend fun getMusicList(context: Context, mediaId: com.jooheon.toyplayer.domain.model.music.MediaId) {
//        val contents = suspendCancellableCoroutine { continuation ->
//            playerController.getMusicListFuture(
//                context = context,
//                mediaId = MediaId.AllSongs,
//                listener = {
//                    val contents = it.mapNotNull { it.toSong() }
//                    continuation.resume(contents)
//                }
//            )
//        }
    }

    private fun onEnqueue(
        playerController: PlayerController,
        songs: List<Song>,
        shuffle: Boolean,
        playWhenReady: Boolean
    ) {
        val shuffled = if(shuffle) songs.shuffled() else songs

        playerController.enqueue(
            songs = shuffled,
            startIndex = 0,
            addNext = false,
            playWhenReady = playWhenReady,
        )
    }

    private fun onSongClick(playerController: PlayerController, song: Song) {
        playerController.play(song)
    }

    private fun onNextClicked(playerController: PlayerController) {
        playerController.seekToNext()
    }

    private fun onPreviousClicked(playerController: PlayerController) {
        playerController.seekToPrevious()
    }
    private fun onDeleteClick(playerController: PlayerController, song: Song) {
        playerController.onDeleteAtPlayingQueue(listOf(song))
    }

    private fun onPlayPauseButtonClicked(playerController: PlayerController, song: Song) {
        if(musicStateHolder.isPlaying.value || musicStateHolder.playWhenReady.value) {
            playerController.pause()
        } else {
            playerController.play(song)
        }
    }

    private fun onShuffleClicked(playerController: PlayerController) {
        playerController.shuffle()
    }

    private fun onRepeatClicked(playerController: PlayerController) {
        playerController.repeat()
    }

    private fun snapTo(playerController: PlayerController, duration: Long) {
        playerController.snapTo(duration)
    }

}