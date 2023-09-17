package com.jooheon.clean_architecture.features.musicservice.usecase

import android.content.Context
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.data.toMediaItem
import com.jooheon.clean_architecture.toyproject.features.common.extension.showToast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * IMPORTANT!!
 * NEVER APPROACH DIRECTLY.
 * Use MusicControllerUsecase.
**/
@Singleton
class MusicController @Inject constructor( // di 옮기고, internal class로 바꿔야함
    @ApplicationContext private val context: Context,
    private val applicationScope: CoroutineScope,
    private val exoPlayer: ExoPlayer,
) : IMusicController {
    private val TAG = MusicService::class.java.simpleName + "@" +  MusicController::class.java.simpleName

    private val _songLibrary = MutableStateFlow(emptyList<Song>())
    val songLibrary = _songLibrary.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.REPEAT_ALL)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode

    private val _shuffleMode = MutableStateFlow(ShuffleMode.SHUFFLE)
    val shuffleMode: StateFlow<ShuffleMode> = _shuffleMode

    private val _currentDuration = MutableStateFlow(0L)
    val currentDuration: StateFlow<Long> = _currentDuration

    private var durationFromPlayerJob: Job? = null

    init {
        initPlaybackState()
    }

    fun registerPlayerListener(listener: Player.Listener) {
        exoPlayer.addListener(listener)
    }
    override suspend fun getPlayingQueue(): List<Song> = withContext(Dispatchers.Main) {
        val songLibrary = songLibrary.value
        val newPlayingQueue = mutableListOf<Song>()
        repeat(exoPlayer.mediaItemCount) {
            val mediaItem = exoPlayer.getMediaItemAt(it)
            val song = songLibrary.firstOrNull { it.id() == mediaItem.mediaId } ?: return@repeat
            newPlayingQueue.add(song)
        }

        return@withContext newPlayingQueue
    }

    override fun mediaItemPosition() = exoPlayer.currentMediaItemIndex

    override suspend fun openPlayingQueue(songs: List<Song>, startIndex: Int) = withContext(Dispatchers.Main) {
        _songLibrary.tryEmit(songs)
        val index = if(songs.size <= startIndex) 0
                    else startIndex
        exoPlayer.setMediaItems(
            songs.map { it.toMediaItem() },
            index,
            C.INDEX_UNSET.toLong()
        )
        exoPlayer.prepare()
    }

    override suspend fun addToPlayingQueue(
        songs: List<Song>,
        position: Int,
    ) = withContext(Dispatchers.Main) {
        songLibrary.value.toMutableList().apply {
            addAll(songs)
        }.run {
            _songLibrary.tryEmit(this)
        }

        exoPlayer.addMediaItems(
            position,
            songs.map { it.toMediaItem() },
        )
        exoPlayer.prepare()
    }
    override suspend fun deleteFromPlayingQueue(
        songIndexList: List<Int>,
        deletedSongs: List<Song>,
    ) = withContext(Dispatchers.Main) {
        val newLibrary = songLibrary.value.toMutableList()
        deletedSongs.forEach { target ->
            songLibrary.value.firstOrNull {
                it.id() == target.id()
            }?.let {
                newLibrary.remove(it)
            }
        }
        _songLibrary.tryEmit(newLibrary)

        songIndexList.forEach {
            exoPlayer.removeMediaItem(it)
        }
        exoPlayer.prepare()
    }

    override suspend fun play(index: Int) = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d("play musicStreamUri - $index")
        exoPlayer.run {
            playWhenReady = true
            seekTo(index, C.INDEX_UNSET.toLong()) // n번째 곡의 X초부터 시작한다.
            play()
        }
    }

    override suspend fun resume() = withContext(Dispatchers.Main) {
        exoPlayer.playWhenReady = true
        exoPlayer.seekTo(exoPlayer.currentMediaItemIndex, currentDuration.value)
        exoPlayer.play()
        debugMessage("resume")
    }

    override suspend fun pause() = withContext(Dispatchers.Main) {
        exoPlayer.run {
            playWhenReady = true
            pause()
        }
        debugMessage("pause")
    }

    override suspend fun stop() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "stop")
        exoPlayer.stop()

        debugMessage("stop")
    }

    override suspend fun previous() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "previous")

        with(exoPlayer) {
            if(hasPreviousMediaItem()) {
                seekToPreviousMediaItem()
                debugMessage("previous")
            }
        }
    }

    override suspend fun next() = withContext(Dispatchers.Main) {
        Timber.tag(TAG).d( "next")
        with(exoPlayer) {
            if(hasNextMediaItem()) {
                seekToNextMediaItem()
                debugMessage("next")
            }
        }
    }

    override suspend fun snapTo(duration: Long, fromUser: Boolean) = withContext(Dispatchers.Main) {
        _currentDuration.tryEmit(duration)
        if(fromUser) {
            exoPlayer.seekTo(duration)
        }
    }

    override suspend fun changeRepeatMode(repeatMode: Int) = withContext(Dispatchers.Main) {
        val value = RepeatMode.values()[repeatMode]
        val (playerState, state) = when(value) {
            RepeatMode.REPEAT_ALL -> Player.REPEAT_MODE_ALL to RepeatMode.REPEAT_ALL
            RepeatMode.REPEAT_ONE -> Player.REPEAT_MODE_ONE to RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_OFF -> Player.REPEAT_MODE_OFF to RepeatMode.REPEAT_OFF
        }

        if(exoPlayer.repeatMode != playerState) {
            exoPlayer.repeatMode = playerState
        }
        _repeatMode.tryEmit(state)

        debugMessage("repeatMode")
    }

    override suspend fun changeShuffleMode(shuffleModeEnabled: Boolean) = withContext(Dispatchers.Main) {
        val shuffleMode = when(shuffleModeEnabled) {
            true -> ShuffleMode.SHUFFLE
            false -> ShuffleMode.NONE
        }
        exoPlayer.shuffleModeEnabled = shuffleModeEnabled
        _shuffleMode.tryEmit(shuffleMode)
        debugMessage("shuffleMode")
    }

    override suspend fun changeSkipDuration() {
//        val skipDuration = settingUseCase.getSkipForwardBackward()
//        runOnUiThread {
//            _skipState.tryEmit(skipDuration)
//        }
    }

    @Synchronized
    fun collectDuration() {
        durationFromPlayerJob?.cancel()
        durationFromPlayerJob = applicationScope.launch(Dispatchers.Main) {
            while (exoPlayer.isPlaying && isActive) {
                val duration = if (exoPlayer.duration != -1L) {
                    exoPlayer.currentPosition
                } else {
                    0L
                }
                Timber.tag(TAG).d( "snapTo: ${duration}, job: ${this}")

                snapTo(
                    duration = duration,
                    fromUser = false
                )

                withContext(Dispatchers.IO) {
                    delay(500)
                }
            }
        }
    }

    private fun initPlaybackState() = applicationScope.launch {
        changeRepeatMode(repeatMode.value.ordinal)
    }

    private suspend fun debugMessage(message: String) = withContext(Dispatchers.Main) {
        context.showToast(message)
    }
}