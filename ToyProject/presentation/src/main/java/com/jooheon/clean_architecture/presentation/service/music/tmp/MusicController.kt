package com.jooheon.clean_architecture.presentation.service.music.tmp

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.extensions.uri
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlaylistUseCase
import com.jooheon.clean_architecture.domain.entity.Entity.RepeatMode
import com.jooheon.clean_architecture.domain.entity.Entity.ShuffleMode
import com.jooheon.clean_architecture.domain.usecase.setting.SettingUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.Runnable
import javax.inject.Inject

class MusicController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationScope: CoroutineScope,
    private val musicPlaylistUseCase: MusicPlaylistUseCase,
    private val settingUseCase: SettingUseCase,
    isPreview: Boolean = false
) : IMusicController{
    private val TAG = MusicService::class.java.simpleName + "@" +  MusicController::class.java.simpleName

    lateinit var exoPlayer: ExoPlayer
        private set

    private var uiThreadHandler: Handler? = null
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _songs = MutableStateFlow(emptyList<Entity.Song>())
    val songs: StateFlow<List<Entity.Song>> = _songs

    private val _currentSongQueue = MutableStateFlow(emptyList<Entity.Song>())
    val currentSongQueue: StateFlow<List<Entity.Song>> = _currentSongQueue

    private val _currentPlayingMusic = MutableStateFlow(Entity.Song.emptySong)
    val currentPlayingMusic: StateFlow<Entity.Song> = _currentPlayingMusic


    private val _repeatMode = MutableStateFlow(RepeatMode.REPEAT_OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _shuffleMode = MutableStateFlow(ShuffleMode.NONE)
    val shuffleMode: StateFlow<ShuffleMode> = _shuffleMode

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    private val _skipState = MutableStateFlow(Entity.SkipForwardBackward.FIVE_SECOND)
    val skipState = _skipState.asStateFlow()
    fun audioSessionId() = exoPlayer.audioSessionId

//    val timePassed = flow {
//        while (true) {
//            val duration = if (exoPlayer.duration != -1L) exoPlayer.currentPosition else 0L
//            emit(duration)
//            delay(500L)
//        }
//    }

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
        if(currentPlayingMusic.value == song) {
            resume()
            return
        }

        runOnUiThread {
            Log.d(TAG, "play - ${song.id}")
            playWithMediaItem(MediaItem.fromUri(song.uri))
            _currentPlayingMusic.tryEmit(song)
        }
    }

    override suspend fun play(_uri: Uri?) {
        val uri = _uri ?: return
        val song = songs.value.firstOrNull { it.uri == uri }

        runOnUiThread {
            if(song != null) {
                playWithMediaItem(MediaItem.fromUri(song.uri))
                _currentPlayingMusic.tryEmit(song)
            } else {
                playWithMediaItem(MediaItem.fromUri(uri))
            }
        }
    }

    private fun playWithMediaItem(mediaItem: MediaItem) {
        exoPlayer.run {
            playWhenReady = true
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    private fun resume() {
        Log.d(TAG, "resume")
        runOnUiThread {
            exoPlayer.play()
        }
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

    override suspend fun snapTo(duration: Long, fromUser: Boolean) {
        runOnUiThread {
            _currentDuration.tryEmit(duration)
            if(fromUser) exoPlayer.seekTo(duration)
        }
    }

    override suspend fun changeRepeatMode() {
        val repeatMode = when(repeatMode.value) {
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.REPEAT_OFF
            RepeatMode.REPEAT_OFF -> RepeatMode.REPEAT_ALL
        }

        runOnUiThread {
            _repeatMode.tryEmit(repeatMode)
        }
    }

    override suspend fun changeShuffleMode() {
        val shuffleMode = when(shuffleMode.value) {
            ShuffleMode.SHUFFLE -> ShuffleMode.NONE
            ShuffleMode.NONE -> ShuffleMode.SHUFFLE
        }

        runOnUiThread {
            _shuffleMode.tryEmit(shuffleMode)
        }
    }

    override suspend fun changeSkipDuration() {
        val skipDuration = settingUseCase.getSkipForwardBackward()
        runOnUiThread {
            _skipState.tryEmit(skipDuration)
        }
    }

    override fun loadPlaylist() {
        musicPlaylistUseCase.loadPlaylist(applicationScope).whenReady { isReady ->
            _songs.tryEmit(musicPlaylistUseCase.allMusic)
            _currentSongQueue.tryEmit(musicPlaylistUseCase.allMusic) // FIXME: 수정!!!
        }
    }

    private fun runOnUiThread(runnable: Runnable?) {
        uiThreadHandler?.post(runnable!!)
    }

    private fun initExoPlayer() {
        // 오디오 포커스 관리: https://developer.android.com/guide/topics/media-apps/audio-focus
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
                        Log.d(TAG, "onPlaybackStateChanged - ${playbackState}")
                        when(playbackState) {
                            ExoPlayer.STATE_IDLE -> Log.d(TAG, "STATE_IDLE")
                            ExoPlayer.STATE_BUFFERING -> Log.d(TAG, "STATE_IDLE")
                            ExoPlayer.STATE_READY -> {
                                Log.d(TAG, "STATE_READY")
                            }
                            ExoPlayer.STATE_ENDED -> {
                                Log.d(TAG, "STATE_ENDED")
                                applicationScope.launch {
                                    this@MusicController.next()
                                }
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        Log.d(TAG, "onIsPlayingChanged - ${isPlaying}")
                        _isPlaying.tryEmit(isPlaying)
                    }

                    override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                        super.onPlaylistMetadataChanged(mediaMetadata)
                        Log.d(TAG, "onPlaylistMetadataChanged")
                    }
                })
            }
    }
}