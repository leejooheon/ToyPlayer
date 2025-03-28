package com.jooheon.toyplayer.features.musicservice.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.Error.Companion.toErrorOrNull
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.errors.RootError
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.ext.enqueue
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.forceSeekToNext
import com.jooheon.toyplayer.features.musicservice.ext.forceSeekToPrevious
import com.jooheon.toyplayer.features.musicservice.ext.playAtIndex
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.asDeferred
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class PlayerController(private val scope: CoroutineScope) {
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

    @OptIn(UnstableApi::class)
    fun getMusicListFuture(
        context: Context,
        mediaId: MediaId,
        listener: (Result<List<MediaItem>, PlaybackDataError>) -> Unit) = executeAfterPrepare {
        val contentFuture = it.getChildren(mediaId.serialize(), 0, Int.MAX_VALUE, null)
        contentFuture.addListener(
            {
                val result = contentFuture.get()
                if(result.resultCode == LibraryResult.RESULT_SUCCESS) {
                    val model = result.value.defaultEmpty().toList()
                    listener.invoke(Result.Success(model))
                } else {
                    val error = result.params?.extras?.let { bundle ->
                        val message = bundle.getString(PlaybackDataError.KEY_MESSAGE).defaultEmpty()
                        PlaybackDataError.InvalidData(message)
                    } ?: PlaybackDataError.InvalidData("getMusicListFuture[$mediaId]: bundle has no data.")

                    Timber.e("error: $error")
                    listener.invoke(Result.Error(error))
                }
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
    fun playPause() = executeAfterPrepare {
        if(it.isPlaying) it.pause()
        else it.play()
    }
    fun playAtIndex(index: Int, time: Long = C.TIME_UNSET) = executeAfterPrepare {
        if(index == C.INDEX_UNSET) return@executeAfterPrepare
        it.playAtIndex(index, time)
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
    fun shuffle(force: Boolean? = null) = executeAfterPrepare { player ->
        force?.let {
            player.shuffleModeEnabled = it
        } ?: run {
            player.shuffleModeEnabled = !(player.shuffleModeEnabled)
        }
    }
    fun repeat() = executeAfterPrepare {
        val value = (it.repeatMode.defaultZero() + 1) % 3
        it.repeatMode = value
    }

    fun enqueue(
        song: Song,
        playWhenReady: Boolean
    ) = executeAfterPrepare { player ->
        val mediaId = MediaId.Playlist(Playlist.PlayingQueue.id)
        player.enqueue(
            mediaItem = song.toMediaItem(mediaId.serialize()),
            playWhenReady = playWhenReady
        )
    }

    fun enqueue(
        songs: List<Song>,
        startIndex: Int = 0,
        startPositionMs: Long = 0L,
        playWhenReady: Boolean
    ) = executeAfterPrepare { player ->
        val mediaId = MediaId.Playlist(Playlist.PlayingQueue.id)
        player.forceEnqueue(
            mediaItems = songs.map { it.toMediaItem(mediaId.serialize()) },
            startIndex = startIndex,
            startPositionMs = startPositionMs,
            playWhenReady = playWhenReady
        )
    }

    fun sendCustomCommand(
        context: Context,
        command: CustomCommand,
        listener: (Result<Bundle, RootError>) -> Unit,
    ) = executeAfterPrepare {
        val listenableFuture = it.sendCustomCommand(command)
        listenableFuture.addListener({
            val result = listenableFuture.get()
            if(result.resultCode == LibraryResult.RESULT_SUCCESS) {
                listener.invoke(Result.Success(result.extras))
            } else {
                val data = "TODO" //TODO result.extras.getString(EssentialPlaybackError.key, null)
                val error = data.toErrorOrNull() ?: PlaybackDataError.InvalidData("sendCustomCommand: bundle has no data. $command")
                Timber.e("sendCustomCommand[$command], error: $error")
                listener.invoke(Result.Error(error))
            }
        }, ContextCompat.getMainExecutor(context))
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
        scope.launch(immediate) {
            val controller = awaitConnect() ?: return@launch
            action(controller)
        }
    }
}