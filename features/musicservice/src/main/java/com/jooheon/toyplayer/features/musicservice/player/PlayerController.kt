package com.jooheon.toyplayer.features.musicservice.player

import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.enqueue
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.forceSeekToNext
import com.jooheon.toyplayer.features.musicservice.ext.forceSeekToPrevious
import com.jooheon.toyplayer.features.musicservice.ext.mediaItems
import com.jooheon.toyplayer.features.musicservice.ext.playAtIndex
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.asDeferred
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class PlayerController(
    private val applicationScope: CoroutineScope,
    private val musicStateHolder: MusicStateHolder,
) {
    private val immediate = Dispatchers.Main.immediate

    private fun newBrowserAsync(context: Context) = MediaBrowser
        .Builder(context, SessionToken(context, ComponentName(context, MusicService::class.java)))
        .buildAsync()
    private lateinit var _controller: ListenableFuture<MediaBrowser>
    private val controller: Deferred<MediaBrowser> get() = _controller.asDeferred()

    fun connect(context: Context) {
        _controller = newBrowserAsync(context)
    }
    fun release() {
        MediaBrowser.releaseFuture(_controller)
    }

    fun getMusicListFuture(
        context: Context,
        mediaId: MediaId,
        listener: (List<MediaItem>) -> Unit) = executeAfterPrepare {
        val contentFuture = it.getChildren(mediaId.serialize(), 0, Int.MAX_VALUE, null)
        contentFuture.addListener(
            {
                val model = contentFuture.get().value
                listener.invoke(model.defaultEmpty().toList())
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun snapTo(position: Long) = executeAfterPrepare {
        it.seekTo(position)
    }
    fun seekToNext() = executeAfterPrepare {
        it.forceSeekToNext()
    }
    fun seekToPrevious() = executeAfterPrepare {
        it.forceSeekToPrevious()
    }
    fun playAtIndex(index: Int, time: Long = C.TIME_UNSET) = executeAfterPrepare {
        if(index == C.INDEX_UNSET) return@executeAfterPrepare
        it.playAtIndex(index, time)
    }
    fun play(song: Song) = executeAfterPrepare { player ->
        val playingQueue = player.mediaItems.map { it.toSong() }

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
                song = song,
                playWhenReady = true
            )
        }
    }
    fun pause() = executeAfterPrepare {
        it.pause()
    }
    fun stop() = executeAfterPrepare {
        it.stop()
    }
    fun playbackSpeed(speed: Float) = executeAfterPrepare {
        require(speed in 0f..1f)
        it.setPlaybackSpeed(speed)
    }
    fun shuffle() = executeAfterPrepare {
        it.shuffleModeEnabled = !(it.shuffleModeEnabled)
    }
    fun repeat() = executeAfterPrepare {
        val value = (it.repeatMode.defaultZero() + 1) % 3
        it.repeatMode = value
    }
    fun enqueue(
        song: Song,
        playWhenReady: Boolean
    ) = executeAfterPrepare { player ->
        val playingQueue = player.mediaItems.map { it.toSong() }

        val index = playingQueue.indexOfFirst {
            it.key() == song.key()
        }

        if(index != C.INDEX_UNSET) {
            player.removeMediaItem(index)
        }

        player.enqueue(
            mediaItem = song.toMediaItem(),
            playWhenReady = playWhenReady
        )
    }

    fun enqueue(
        songs: List<Song>,
        addNext: Boolean,
        playWhenReady: Boolean
    ) = executeAfterPrepare { player ->
        if (addNext) { // TabToSelect
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
            val newMediaItems = songs.map { it.toMediaItem() }

            player.forceEnqueue(
                mediaItems = newMediaItems,
                startIndex = 0,
                startPositionMs = C.TIME_UNSET,
                playWhenReady = playWhenReady
            )
        }
    }
    fun onDeleteAtPlayingQueue(
        songs: List<Song>
    ) = executeAfterPrepare { player ->
        val playingQueue = player.mediaItems.map { it.toSong() }

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

    private suspend fun awaitConnect(): MediaBrowser? {
        return try {
            controller.await()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Timber.e(e, "Error while connecting to media controller")
            null
        }
    }

    private inline fun executeAfterPrepare(crossinline action: suspend (MediaBrowser) -> Unit) {
        applicationScope.launch(immediate) {
            val controller = awaitConnect() ?: return@launch
            action(controller)
        }
    }
}