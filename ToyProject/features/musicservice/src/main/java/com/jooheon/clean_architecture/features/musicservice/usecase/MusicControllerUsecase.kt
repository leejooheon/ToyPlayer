package com.jooheon.clean_architecture.features.musicservice.usecase

import android.app.Activity
import android.content.*
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.MusicService.Companion.MUSIC_DURATION
import com.jooheon.clean_architecture.features.musicservice.MusicService.Companion.MUSIC_STATE
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.ext.playbackErrorReason
import com.jooheon.clean_architecture.features.musicservice.ext.toMediaItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import timber.log.Timber
import javax.inject.Singleton

@UnstableApi
class MusicControllerUsecase(
    private val context: Context,
    private val applicationScope: CoroutineScope,
    private val musicController: MusicController,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUsecase::class.java.simpleName
    private var musicService: MusicService? = null
    private val connectionMap = WeakHashMap<Context, ServiceConnection>()

    private val _songLibrary = MutableStateFlow(emptyList<Song>())
//    val songLibrary = _songLibrary.asStateFlow()

    private val _playingQueue = MutableStateFlow<List<Song>>(emptyList())
    val playingQueue = _playingQueue.asStateFlow()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _timePassed = MutableStateFlow(0L)
    val timePassed = _timePassed.asStateFlow()

    private val _playerState = MutableStateFlow<Int?>(null)
    val playerState = _playerState.asStateFlow()

    private val _musicStreamErrorChannel = Channel<Resource.Failure>()
    val musicStreamErrorChannel = _musicStreamErrorChannel.receiveAsFlow()

    init {
        Timber.tag(TAG).d( "musicController - ${musicController}")
        collectMusicState()
        collectTimelineWindows()
        collectNullableWindow()
        collectMediaItemIndex()
        collectIsPlaying()
        collectDuration()
        collectRepeatMode()
        collectShuffleMode()
        collectExoPlayerState()
        collectPlaybackException()

        initPlayingQueue()
    }

    private fun commandToService() {
        Timber.tag(TAG).d( "commandToService")
        if(serviceIntent == null) return

        serviceIntent.putExtra(MUSIC_STATE, musicState.value)
        serviceIntent.putExtra(MUSIC_DURATION, timePassed.value)
        MusicService.startService(context, serviceIntent)
    }
    private fun collectMusicState() = applicationScope.launch(Dispatchers.IO) {
        musicState.collectLatest {
            Timber.tag(TAG).d( "collectMusicState")
            commandToService()
        }
    }
    private fun collectDuration() = applicationScope.launch {
        musicController.currentDuration.collectLatest { currentDuration ->
            _timePassed.update { currentDuration }
        }
    }
    private fun collectTimelineWindows() = applicationScope.launch {
        musicController.timelineWindows.collectLatest { timelineWindows ->
            val songLibrary = _songLibrary.value
            Timber.tag(TAG).d("collectTimelineWindows: origin: ${timelineWindows.size}, filtered: ${songLibrary.size}")

            val newPlayingQueue = mutableListOf<Song>()
            timelineWindows.forEach { window ->
                val song = songLibrary.firstOrNull {
                    it.id() == window.mediaItem.mediaId
                } ?: return@forEach
                Timber.tag(TAG).d("collectTimelineWindows: ${song.title}")
                newPlayingQueue.add(song)
            }
            if(newPlayingQueue.isNotEmpty()) {
                playingQueueUseCase.updatePlayingQueue(
                    song = newPlayingQueue.toTypedArray()
                )
            }
            _playingQueue.tryEmit(newPlayingQueue)
            _musicState.update {
                it.copy(
                    playingQueue = newPlayingQueue
                )
            }
        }
    }
    private fun collectNullableWindow() = applicationScope.launch {
        musicController.nullableWindow.collectLatest { window ->
            val songLibrary = _songLibrary.value

            val song = songLibrary.firstOrNull {
                it.id() == window?.mediaItem?.mediaId
            } ?: Song.default

            updateCurrentPlayingMusic(song)
        }
    }
    private fun collectMediaItemIndex() = applicationScope.launch {
        musicController.mediaItemIndex.collectLatest {
            playingQueueUseCase.setPlayingQueuePosition(it)
        }
    }
    private fun collectIsPlaying() = applicationScope.launch {
        musicController.isPlaying.collectLatest { isPlaying ->
            Timber.tag(TAG).d( "collectIsPlaying - ${isPlaying}")
            _musicState.update {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }
    }
    private fun collectRepeatMode() = applicationScope.launch {
        musicController.repeatMode.collectLatest { repeatMode ->
            Timber.tag(TAG).d( "collectRepeatMode - ${repeatMode}")
            _musicState.update {
                it.copy(
                    repeatMode = RepeatMode.getByValue(repeatMode)
                )
            }
        }
    }
    private fun collectShuffleMode() = applicationScope.launch {
        musicController.shuffleMode.collectLatest { shuffleMode ->
            Timber.tag(TAG).d( "collectShuffleMode - ${shuffleMode}")
            _musicState.update {
                it.copy(
                    shuffleMode = ShuffleMode.getByValue(shuffleMode)
                )
            }
        }
    }
    private fun collectExoPlayerState() = applicationScope.launch {
        musicController.exoPlayerState.collectLatest { state ->
            _playerState.tryEmit(state)

            if(state == ExoPlayer.STATE_READY) {
                updateCurrentPlayingMusic(musicState.value.currentPlayingMusic)
            }

            _musicState.update {
                it.copy(
                    isBuffering = state == ExoPlayer.STATE_BUFFERING
                )
            }
        }
    }
    private fun collectPlaybackException() = applicationScope.launch {
        musicController.playbackExceptionChannel.collectLatest { error ->
            Timber.tag(TAG).e("collectPlaybackException: ${error.errorCode.playbackErrorReason()}, ${error.message}")

            val failureStatus = when(error.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> FailureStatus.NO_INTERNET
                else -> FailureStatus.OTHER
            }

            _musicStreamErrorChannel.send(
                Resource.Failure(
                    failureStatus = failureStatus,
                    code = error.errorCode,
                    message = error.message
                )
            )
        }
    }
    fun addToPlayingQueue(song: Song) = applicationScope.launch {
        val index = playingQueue.value.indexOfFirst {
            it.id() == song.id()
        }

        if(index != C.INDEX_UNSET) {
            musicController.removeMeidaItems(listOf(index))
        } else {
            _songLibrary.update { it.toMutableList().apply { add(song) } }
        }

        musicController.addMediaItems(
            mediaItems = listOf(song).map { it.toMediaItem() },
            addNext = true,
            playWhenReady = true
        )
    }

    fun onPlayAtPlayingQueue(
        songs: List<Song>,
        addToPlayingQueue: Boolean,
        playWhenReady: Boolean
    ) = applicationScope.launch(Dispatchers.IO) {
        if (addToPlayingQueue) { // TabToSelect
            val songLibrary = _songLibrary.value

            val filtered = songs.filter { song ->
                songLibrary.none { it.id() == song.id() }
            }

            _songLibrary.update {
                it.toMutableList().apply { addAll(filtered) }
            }

            musicController.addMediaItems(
                mediaItems = filtered.map { it.toMediaItem() },
                addNext = false,
                playWhenReady = true //playWhenReady,
            )
        } else { // TabToPlay
            _songLibrary.update { songs }

            musicController.setMediaItems(
                mediaItems = songs.map{ it.toMediaItem() },
                startIndex = 0,
                playWhenReady = playWhenReady
            )
        }
    }

    fun onDeleteAtPlayingQueue(songs: List<Song>) = applicationScope.launch(Dispatchers.IO) {
        val playingQueue = _playingQueue.value

        val songIndexList = songs.filter {
            it != Song.default
        }.map { targetSong ->
            playingQueue.indexOfFirst { it.id() == targetSong.id() }
        }.filter {
            it != -1
        }

        musicController.removeMeidaItems(
            mediaItemIndexes = songIndexList,
        )
    }

    fun onPlay(
        song: Song = musicState.value.currentPlayingMusic,
    ) = applicationScope.launch(Dispatchers.IO) {
        val state = musicState.value

        val index = state.playingQueue.indexOfFirst {
            it.id() == song.id()
        }

        musicController.play(
            index = index,
            seekTo = C.TIME_UNSET,
            playWhenReady = true
        )
    }
    fun onPause() = applicationScope.launch(Dispatchers.IO) {
        musicController.pause()
    }
    fun onStop() = applicationScope.launch(Dispatchers.IO) {
        musicController.stop()
    }
    fun onNext() = applicationScope.launch(Dispatchers.Main) {
        musicController.next()
    }
    fun onPrevious() = applicationScope.launch(Dispatchers.Main) {
        musicController.previous()
    }
    fun snapTo(duration: Long) = applicationScope.launch(Dispatchers.Main) {
        musicController.snapTo(
            duration = duration,
            fromUser = true
        )
    }

    fun onShuffleButtonPressed(shuffleModeEnabled: Boolean) = applicationScope.launch(Dispatchers.IO) {
        musicController.changeShuffleMode(shuffleModeEnabled)
    }
    fun onRepeatButtonPressed(repeatMode: RepeatMode) = applicationScope.launch(Dispatchers.IO) {
        musicController.changeRepeatMode(repeatMode.ordinal)
    }
    private fun updateCurrentPlayingMusic(song: Song) {
        Timber.tag(TAG).d("updateCurrentPlayingMusic: ${song.title}, Id: ${song.id()}")

        _timePassed.update { 0L }
        _musicState.update {
            it.copy(currentPlayingMusic = song)
        }
    }

    fun bindToService(context: Context): ServiceToken? {
        val realActivity = (context as Activity).parent ?: context
        val contextWrapper = ContextWrapper(realActivity)
        val intent = Intent(contextWrapper, MusicService::class.java)

        try {
            contextWrapper.startService(intent)
        } catch (ignored: IllegalStateException) {
            runCatching {
                ContextCompat.startForegroundService(context, intent)
            }
        }

        if (contextWrapper.bindService(
                Intent().setClass(contextWrapper, MusicService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        ) {
            connectionMap[contextWrapper] = serviceConnection
            return ServiceToken(contextWrapper)
        }

        return null
    }

    fun unbindToService(serviceToken: ServiceToken?) {
        if (serviceToken == null) {
            return
        }

        val contextWrapper = serviceToken.wrappedContext
        val binder = connectionMap.remove(contextWrapper) ?: return
        contextWrapper.unbindService(binder)

        if (connectionMap.isEmpty()) {
            musicService = null
        }
    }

    private fun initPlayingQueue() = applicationScope.launch {
        Timber.tag("Jooheon").e("initPlayingQueue: $musicController")
        playingQueueUseCase.getPlayingQueue().onEach {
            if(it is Resource.Success) {
                val playingQueue = it.value
                _songLibrary.tryEmit(playingQueue)
                musicController.setMediaItems(
                    mediaItems = playingQueue.map { it.toMediaItem() },
                    startIndex = playingQueueUseCase.getPlayingQueuePosition(),
                    playWhenReady = false
                )
            }
        }.launchIn(this)
    }

    private val serviceIntent = Intent(context, MusicService::class.java)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MediaPlayerServiceBinder
            musicService = binder.getService()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            musicService = null
        }
    }

    class ServiceToken internal constructor(internal var wrappedContext: ContextWrapper)
}