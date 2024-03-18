package com.jooheon.toyplayer.features.musicservice.usecase

import androidx.media3.common.C
import androidx.media3.common.Player
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.ShuffleMode
import com.jooheon.toyplayer.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaybackUseCase(
    private val musicStateHolder: MusicStateHolder,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    internal suspend fun initialize(player: Player, scope: CoroutineScope) {
        initPlaybackOptions(player)
        initPlayingQueue(player)

        collectMediaItem(scope)
        collectPlayingQueue(scope)
        collectPlaybackOptions(scope)
    }

    private fun collectPlayingQueue(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItems.collectLatest {
            val songs = it.map { it.toSong() }
            playingQueueUseCase.setPlayingQueue(songs)
        }
    }

    private fun collectMediaItem(scope: CoroutineScope) = scope.launch {
        musicStateHolder.mediaItem.collectLatest { mediaItem ->
            mediaItem ?: return@collectLatest
            val key = mediaItem.mediaId.toLongOrNull() ?: return@collectLatest
            playingQueueUseCase.setRecentMediaItemKey(key)
        }
    }

    private fun collectPlaybackOptions(scope: CoroutineScope) = scope.launch {
        launch {
            musicStateHolder.shuffleMode.collectLatest {
                playingQueueUseCase.shuffleModeChanged(it)
            }
        }

        launch {
            musicStateHolder.repeatMode.collectLatest {
                playingQueueUseCase.repeatModeChanged(it)
            }
        }
    }

    private suspend fun initPlayingQueue(player: Player) = withContext(Dispatchers.IO) {
        playingQueueUseCase.playingQueue().onEach {
            if(it is Resource.Success) {
                val playingQueue = it.value

                val newMediaItems = playingQueue.map { it.toMediaItem() }
                withContext(Dispatchers.Main.immediate) {
                    val key = playingQueueUseCase.getRecentMediaItemKey()
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

    private suspend fun initPlaybackOptions(player: Player) = withContext(Dispatchers.Main.immediate) {
        val repeatMode = playingQueueUseCase.repeatMode()
        val shuffleMode = playingQueueUseCase.shuffleMode()

        player.repeatMode = repeatMode.ordinal
        player.shuffleModeEnabled = shuffleMode == ShuffleMode.SHUFFLE
    }
}