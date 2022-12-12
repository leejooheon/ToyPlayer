package com.jooheon.clean_architecture.presentation.service.music.tmp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.compositionLocalOf
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.extensions.uri
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.security.PrivilegedAction
import javax.inject.Inject

class MusicController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicPlayerUseCase: MusicPlayerUseCase,
    isPreview: Boolean = false
) : IMusicController{
    private val TAG = MusicService::class.java.simpleName + "@" +  MusicController::class.java.simpleName

    private lateinit var exoPlayer: ExoPlayer
    private var uiThreadHandler: Handler? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _songs = MutableStateFlow(emptyList<Entity.Song>())
    val songs: StateFlow<List<Entity.Song>> = _songs

    private val _currentSongQueue = MutableStateFlow(emptyList<Entity.Song>())
    val currentSongQueue: StateFlow<List<Entity.Song>> = _currentSongQueue

    private val _currentPlayingMusic = MutableStateFlow(Entity.Song.emptySong)
    val currentPlayingMusic: StateFlow<Entity.Song> = _currentPlayingMusic

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    init {
        if(isPreview) {
            /** nothing **/
        } else {
            initExoPlayer()
            uiThreadHandler = Handler(Looper.getMainLooper())
        }
    }

    override fun updateQueueSong(songs: List<Entity.Song>) {
        Log.d(TAG, "updateQueueSong")
    }

    override suspend fun play(song: Entity.Song) {
        runOnUiThread {
            if(currentPlayingMusic.value == song) {
                resume()
                return@runOnUiThread
            }
            Log.d(TAG, "play")

            _currentPlayingMusic.tryEmit(song)
            exoPlayer.run {
                playWhenReady = true
                setMediaItem(MediaItem.fromUri(song.uri))
                prepare()
                play()
            }
        }
    }

    private fun resume() {
        Log.d(TAG, "resume")
        exoPlayer.play()
    }

    override suspend fun pause() {
        runOnUiThread {
            _isPlaying.tryEmit(false)
            exoPlayer.run {
                playWhenReady = true
                pause()
            }
        }
    }

    override suspend fun stop() {
        Log.d(TAG, "stop")
        runOnUiThread {
            _currentPlayingMusic.tryEmit(Entity.Song.emptySong)
            _isPlaying.tryEmit(false)

            exoPlayer.stop()
        }
    }

    override suspend fun previous() {
        Log.d(TAG, "previous")
        val currentSongQueue = currentSongQueue.value
        val currentIndex = currentSongQueue.indexOfFirst {
            it.id == currentPlayingMusic.value.id
        }

        val previousSong = when {
            currentIndex == 0 -> currentSongQueue.last()
            currentIndex >= 1 -> currentSongQueue.get(currentIndex - 1)
            else -> currentSongQueue.first()
        }

        play(previousSong)
    }

    override suspend fun next() {
        val currentSongQueue = currentSongQueue.value
        val currentIndex = currentSongQueue.indexOfFirst {
            it.id == currentPlayingMusic.value.id
        }

        val nextSong = when {
            currentIndex >= currentSongQueue.lastIndex -> currentSongQueue.first()
            currentIndex != -1 -> currentSongQueue.get(currentIndex + 1)
            else -> currentSongQueue.first()
        }

        play(nextSong)
    }

    override suspend fun snapTo(duration: Long) {
        runOnUiThread {
            _currentDuration.tryEmit(duration)
            exoPlayer.seekTo(duration)
        }
    }

    override fun loadMusic(scope: CoroutineScope) {
        musicPlayerUseCase.loadMusic(scope).whenReady { isReady ->
            _songs.tryEmit(musicPlayerUseCase.allMusic)
            _currentSongQueue.tryEmit(musicPlayerUseCase.allMusic) // FIXME: 수정!!!
        }
    }

    private fun runOnUiThread(runnable: Runnable?) {
        uiThreadHandler?.post(runnable!!)
    }

    private fun initExoPlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()
            .apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when(playbackState) {
                            ExoPlayer.STATE_IDLE -> Log.d(TAG, "STATE_IDLE")
                            ExoPlayer.STATE_BUFFERING -> Log.d(TAG, "STATE_IDLE")
                            ExoPlayer.STATE_READY -> Log.d(TAG, "STATE_READY")
                            ExoPlayer.STATE_ENDED -> Log.d(TAG, "STATE_ENDED")
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        Log.d(TAG, "onIsPlayingChanged")
                        _isPlaying.tryEmit(isPlaying)
                    }
                })
            }
    }
}