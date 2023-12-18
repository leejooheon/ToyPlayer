package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.media3.common.C
import androidx.media3.common.Player
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultZero
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.ext.enqueue
import com.jooheon.clean_architecture.features.musicservice.ext.forceEnqueue
import com.jooheon.clean_architecture.features.musicservice.ext.forceSeekToNext
import com.jooheon.clean_architecture.features.musicservice.ext.forceSeekToPrevious
import com.jooheon.clean_architecture.features.musicservice.ext.lastIndex
import com.jooheon.clean_architecture.features.musicservice.ext.playAtIndex
import com.jooheon.clean_architecture.features.musicservice.ext.shuffledItems
import com.jooheon.clean_architecture.features.musicservice.ext.toMediaItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import timber.log.Timber

class MusicControllerUseCase(
    private val applicationScope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUseCase::class.java.simpleName
    private val immediate = Dispatchers.Main.immediate
    private var player: Player? = null

    init {
        collectShuffleMode()
        collectCurrentWindow()
    }

    fun setPlayer(player: Player) {
        this.player = player
        initPlayingQueue()
        initPlaybackOptions()
    }

    private fun collectShuffleMode() = applicationScope.launch {
        musicStateHolder.shuffleMode.collectLatest { shuffleMode ->
            Timber.tag(TAG).d( "collectShuffleMode - $shuffleMode")
            shuffle(
                playWhenReady = musicStateHolder.isPlaying.value
            )
        }
    }

    private fun collectCurrentWindow() = applicationScope.launch {
        musicStateHolder.currentWindow.collectLatest { window ->
            window ?: return@collectLatest
            val key = window.mediaItem.mediaId.toLongOrNull() ?: return@collectLatest
            playingQueueUseCase.setPlayingQueueKey(key)
        }
    }

    fun enqueue(
        song: Song,
        playWhenReady: Boolean
    ) = applicationScope.launch(immediate) {
        val player = player ?: return@launch
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val index = playingQueue.indexOfFirst {
            it.key() == song.key()
        }

        if(index != C.INDEX_UNSET) {
            player.removeMediaItem(index)
        }
        musicStateHolder.enqueueSongLibrary(listOf(song))

        player.enqueue(
            mediaItem = song.toMediaItem(),
            playWhenReady = playWhenReady
        )
    }

    fun enqueue(
        songs: List<Song>,
        addNext: Boolean,
        playWhenReady: Boolean
    ) = applicationScope.launch(immediate) {
        val player = player ?: return@launch

        if (addNext) { // TabToSelect
            musicStateHolder.enqueueSongLibrary(songs)

            val newMediaItems = songs.distinctBy {
                it.key() // remove duplicate
            }.map {
                it.toMediaItem()
            }

            player.enqueue(
                mediaItems = newMediaItems,
                playWhenReady = playWhenReady
            )
        } else { // TabToPlay
            musicStateHolder.enqueueSongLibrary(songs)
            val newMediaItems = songs.map { it.toMediaItem() }

            player.forceEnqueue(
                mediaItems = newMediaItems,
                startIndex = 0,
                startPositionMs = C.TIME_UNSET,
                playWhenReady = playWhenReady
            )
        }
    }

    fun onDeleteAtPlayingQueue(songs: List<Song>) = applicationScope.launch(immediate) {
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val songIndexList = songs.filter {
            it != Song.default
        }.map { targetSong ->
            playingQueue.indexOfFirst { it.key() == targetSong.key() }
        }.filter {
            it != -1
        }

        songIndexList.forEach {
            player?.removeMediaItem(it)
        }
    }

    fun onPlay(
        song: Song = musicStateHolder.musicState.value.currentPlayingMusic
    ) = applicationScope.launch(immediate) {
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val index = playingQueue.indexOfFirst {
            it.key() == song.key()
        }

        if(index != C.INDEX_UNSET) {
            player?.playAtIndex(index, C.TIME_UNSET)
        } else {
            enqueue(
                song = song,
                playWhenReady = true
            )
        }
    }
    fun shuffle(playWhenReady: Boolean) = applicationScope.launch(immediate) {
        val player = player ?: return@launch
        val shuffledItems = player.shuffledItems()

        player.forceEnqueue(
            mediaItems = shuffledItems,
            startIndex = 0,
            startPositionMs = musicStateHolder.musicState.value.timePassed,
            playWhenReady = playWhenReady
        )
    }
    fun onPause() = applicationScope.launch(immediate) {
        player?.pause()
    }
    fun onStop() = applicationScope.launch(immediate) {
        player?.stop()
    }
    fun onNext() = applicationScope.launch(immediate) {
        player?.forceSeekToNext()
    }
    fun onPrevious() = applicationScope.launch(immediate) {
        player?.forceSeekToPrevious()
    }
    fun snapTo(duration: Long) = applicationScope.launch(immediate) {
        player?.seekTo(duration)
    }

    fun onShuffleButtonPressed() = applicationScope.launch(immediate) {
        val shuffleMode = musicStateHolder.shuffleMode.value
        player?.shuffleModeEnabled = !shuffleMode
    }
    fun onRepeatButtonPressed() = applicationScope.launch(immediate) {
        val value = (player?.repeatMode.defaultZero() + 1) % 3
        player?.repeatMode = value
    }

    fun release() {
        player = null
    }

    private fun initPlayingQueue() = applicationScope.launch(Dispatchers.IO) {
        playingQueueUseCase.playingQueue().onEach {
            if(it is Resource.Success) {
                val playingQueue = it.value
                musicStateHolder.enqueueSongLibrary(playingQueue)

                val newMediaItems = playingQueue.map { it.toMediaItem() }
                withContext(immediate) {
                    val key = playingQueueUseCase.getPlayingQueueKey()
                    val index = newMediaItems.indexOfFirst {
                        it.mediaId == key.toString()
                    }
                    player?.forceEnqueue(
                        mediaItems = newMediaItems,
                        startIndex = index,
                        startPositionMs = C.TIME_UNSET,
                        playWhenReady = false
                    )
                }
            }
        }.launchIn(this)
    }

    private fun initPlaybackOptions() = applicationScope.launch(immediate) {
        val player = player ?: return@launch

        val repeatMode = playingQueueUseCase.repeatMode()
        val shuffleMode = playingQueueUseCase.shuffleMode()

        player.repeatMode = repeatMode.ordinal
        player.shuffleModeEnabled = shuffleMode == ShuffleMode.SHUFFLE
    }
}