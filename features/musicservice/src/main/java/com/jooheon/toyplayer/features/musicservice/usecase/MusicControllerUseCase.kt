package com.jooheon.toyplayer.features.musicservice.usecase

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.ext.enqueue
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.forceSeekToNext
import com.jooheon.toyplayer.features.musicservice.ext.forceSeekToPrevious
import com.jooheon.toyplayer.features.musicservice.ext.playAtIndex
import com.jooheon.toyplayer.features.musicservice.ext.shuffledItems
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.asDeferred
import timber.log.Timber

class MusicControllerUseCase(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUseCase::class.java.simpleName

    private var _controller: Deferred<MediaController> = newControllerAsync()
    private val immediate = Dispatchers.Main.immediate

    init {
        collectShuffleMode()
        collectCurrentWindow()
    }

    private fun newControllerAsync() = MediaController
        .Builder(context, SessionToken(context, ComponentName(context, MusicService::class.java)))
        .buildAsync()
        .asDeferred()

    private val controller: Deferred<MediaController>
        get() {
            if (_controller.isCompleted) {
                val completedController = _controller.getCompleted()
                if (!completedController.isConnected) {
                    completedController.release()
                    _controller = newControllerAsync()
                }
            }
            return _controller
        }

    private fun collectShuffleMode() = applicationScope.launch {
        musicStateHolder.shuffleMode.collectLatest { shuffleMode ->
            Timber.tag(TAG).d( "collectShuffleMode - $shuffleMode")
//            shuffle(
//                playWhenReady = musicStateHolder.isPlaying.value
//            )
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
    ) = executeAfterPrepare { controller ->
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val index = playingQueue.indexOfFirst {
            it.key() == song.key()
        }

        if(index != C.INDEX_UNSET) {
            controller.removeMediaItem(index)
        }
        musicStateHolder.enqueueSongLibrary(listOf(song))
        controller.enqueue(
            mediaItem = song.toMediaItem(),
            playWhenReady = playWhenReady
        )
    }

    fun enqueue(
        songs: List<Song>,
        addNext: Boolean,
        playWhenReady: Boolean
    ) = executeAfterPrepare { controller ->

        if (addNext) { // TabToSelect
            musicStateHolder.enqueueSongLibrary(songs)

            val newMediaItems = songs.distinctBy {
                it.key() // remove duplicate
            }.map {
                it.toMediaItem()
            }

            controller.enqueue(
                mediaItems = newMediaItems,
                playWhenReady = playWhenReady
            )
        } else { // TabToPlay
            musicStateHolder.enqueueSongLibrary(songs)
            val newMediaItems = songs.map { it.toMediaItem() }

            controller.forceEnqueue(
                mediaItems = newMediaItems,
                startIndex = 0,
                startPositionMs = C.TIME_UNSET,
                playWhenReady = playWhenReady
            )
        }
    }

    fun onDeleteAtPlayingQueue(songs: List<Song>) = executeAfterPrepare { controller ->
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val songIndexList = songs.filter {
            it != Song.default
        }.map { targetSong ->
            playingQueue.indexOfFirst { it.key() == targetSong.key() }
        }.filter {
            it != -1
        }

        songIndexList.forEach {
            controller.removeMediaItem(it)
        }
    }

    fun onPlay(
        song: Song = musicStateHolder.musicState.value.currentPlayingMusic
    ) = executeAfterPrepare { controller ->
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val index = playingQueue.indexOfFirst {
            it.key() == song.key()
        }

        if(index != C.INDEX_UNSET) {
            controller.playAtIndex(
                index = index,
                duration = controller.currentPosition
            )
        } else {
            enqueue(
                song = song,
                playWhenReady = true
            )
        }
    }

    fun shuffle(playWhenReady: Boolean) = executeAfterPrepare { controller ->
        val shuffledItems = controller.shuffledItems()

        controller.forceEnqueue(
            mediaItems = shuffledItems,
            startIndex = 0,
            startPositionMs = musicStateHolder.musicState.value.timePassed,
            playWhenReady = playWhenReady
        )
    }
    fun onPause() = executeAfterPrepare { controller ->
        controller.pause()
    }
    fun onStop() = executeAfterPrepare { controller ->
        controller.stop()
    }
    fun onNext() = executeAfterPrepare { controller ->
        controller.forceSeekToNext()
    }
    fun onPrevious() = executeAfterPrepare { controller ->
        controller.forceSeekToPrevious()
    }
    fun snapTo(duration: Long) = executeAfterPrepare { controller ->
        controller.seekTo(duration)
    }

    fun onShuffleButtonPressed() = executeAfterPrepare { controller ->
        val shuffleMode = musicStateHolder.shuffleMode.value
        controller.shuffleModeEnabled = !shuffleMode
    }
    fun onRepeatButtonPressed() = executeAfterPrepare { controller ->
        val value = (controller.repeatMode.defaultZero() + 1) % 3
        controller.repeatMode = value
    }

    private fun maybePrepare(controller: MediaController): Boolean {
        if(controller.currentMediaItem != null &&
           controller.playbackState in listOf(Player.STATE_READY, Player.STATE_BUFFERING)
        ) {
           return true
        }
        return false
    }

    private suspend fun initPlayingQueue(controller: MediaController) = withContext(Dispatchers.IO) {
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
                    controller.forceEnqueue(
                        mediaItems = newMediaItems,
                        startIndex = index,
                        startPositionMs = C.TIME_UNSET,
                        playWhenReady = false
                    )
                }
            }
        }.launchIn(this)
    }

    private suspend fun initPlaybackOptions(controller: MediaController) = withContext(immediate) {
        val repeatMode = playingQueueUseCase.repeatMode()
        val shuffleMode = playingQueueUseCase.shuffleMode()

        controller.repeatMode = repeatMode.ordinal
        controller.shuffleModeEnabled = shuffleMode == ShuffleMode.SHUFFLE
    }

    private inline fun executeAfterPrepare(crossinline action: suspend (MediaController) -> Unit) {
        applicationScope.launch(immediate) {
            val controller = awaitConnect() ?: return@launch
            if (!maybePrepare(controller)) {
                initPlayingQueue(controller)
                initPlaybackOptions(controller)
            }
            action(controller)
        }
    }

    suspend fun awaitConnect(): MediaController? {
        return try {
            controller.await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e("Error while connecting to media controller")
            null
        }
    }
}