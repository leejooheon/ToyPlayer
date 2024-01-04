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
    private val applicationScope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUseCase::class.java.simpleName
    private val immediate = Dispatchers.Main.immediate

    init {
        collectShuffleMode()
        collectCurrentWindow()
    }

    fun initialize(player: Player) = applicationScope.launch(immediate) {
        initPlayingQueue(player)
        initPlaybackOptions(player)
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

    suspend fun enqueue(
        player: Player,
        song: Song,
        playWhenReady: Boolean
    ) = withContext(immediate){
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

    suspend fun enqueue(
        player: Player,
        songs: List<Song>,
        addNext: Boolean,
        playWhenReady: Boolean
    ) = withContext(immediate){
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

    suspend fun onDeleteAtPlayingQueue(
        player: Player,
        songs: List<Song>
    ) = withContext(immediate){
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val songIndexList = songs.filter {
            it != Song.default
        }.map { targetSong ->
            playingQueue.indexOfFirst { it.key() == targetSong.key() }
        }.filter {
            it != -1
        }

        songIndexList.forEach {
            player.removeMediaItem(it)
        }
    }

    suspend fun onPlay(
        player: Player,
        song: Song = musicStateHolder.musicState.value.currentPlayingMusic
    ) = withContext(immediate){
        val playingQueue = playingQueueUseCase.getPlayingQueue()

        val index = playingQueue.indexOfFirst {
            it.key() == song.key()
        }

        if(index != C.INDEX_UNSET) {
            player.playAtIndex(
                index = index,
                duration = player.currentPosition
            )
        } else {
            enqueue(
                player = player,
                song = song,
                playWhenReady = true
            )
        }
    }

    suspend fun shuffle(
        player: Player,
        playWhenReady: Boolean
    ) = withContext(immediate){
        val shuffledItems = player.shuffledItems()

        player.forceEnqueue(
            mediaItems = shuffledItems,
            startIndex = 0,
            startPositionMs = musicStateHolder.musicState.value.timePassed,
            playWhenReady = playWhenReady
        )
    }
    suspend fun onPause(player: Player) = withContext(immediate){
        player.pause()
    }
    suspend fun onStop(player: Player) = withContext(immediate){
        player.stop()
    }
    suspend fun onNext(player: Player) = withContext(immediate){
        player.forceSeekToNext()
    }
    suspend fun onPrevious(player: Player) = withContext(immediate){
        player.forceSeekToPrevious()
    }
    suspend fun snapTo(player: Player, duration: Long) = withContext(immediate){
        player.seekTo(duration)
    }

    suspend fun onShuffleButtonPressed(player: Player) = withContext(immediate){
        val shuffleMode = musicStateHolder.shuffleMode.value
        player.shuffleModeEnabled = !shuffleMode
    }
    suspend fun onRepeatButtonPressed(player: Player) = withContext(immediate){
        val value = (player.repeatMode.defaultZero() + 1) % 3
        player.repeatMode = value
    }

    private suspend fun initPlayingQueue(player: Player) = withContext(Dispatchers.IO) {
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
                    player.forceEnqueue(
                        mediaItems = newMediaItems,
                        startIndex = index,
                        startPositionMs = C.TIME_UNSET,
                        playWhenReady = false
                    )
                }
            }
        }.launchIn(this)
    }

    private suspend fun initPlaybackOptions(player: Player) = withContext(immediate) {
        val repeatMode = playingQueueUseCase.repeatMode()
        val shuffleMode = playingQueueUseCase.shuffleMode()

        player.repeatMode = repeatMode.ordinal
        player.shuffleModeEnabled = shuffleMode == ShuffleMode.SHUFFLE
    }
}