package com.jooheon.clean_architecture.features.musicservice.usecase

import android.app.Activity
import android.content.*
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.MusicService.Companion.MUSIC_DURATION
import com.jooheon.clean_architecture.features.musicservice.MusicService.Companion.MUSIC_STATE
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class MusicControllerUsecase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val applicationScope: CoroutineScope,
    private val musicController: MusicController,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUsecase::class.java.simpleName

    private var musicService: MusicService? = null
    private val connectionMap = WeakHashMap<Context, ServiceConnection>()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _playingQueue = MutableStateFlow(emptyList<Song>())
    val playingQueue: StateFlow<List<Song>> = _playingQueue

    private val _timePassed = MutableStateFlow(0L)
    val timePassed = _timePassed.asStateFlow()

    private val _exoPlayerState = MutableStateFlow<Int?>(null)
    val exoPlayerState = _exoPlayerState.asStateFlow()

    init {
        Timber.tag(TAG).d( "musicController - ${musicController}")
        collectMusicState()
        collectPlayingQueue()
        collectCurrentSong()
        collectIsPlaying()
        collectDuration()
        collectRepeatMode()
        collectShuffleMode()
        collectExoPlayerState()
        collectPlayingQueueFromProvider()
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

    private fun collectPlayingQueue() = applicationScope.launch {
        musicController.playingQueue.collectLatest { playingQueue ->
            Timber.tag(TAG).d( "collectPlayingQueueFromPlayer - ${playingQueue.size}")
            _musicState.update { // it will be removed
                it.copy(
                    playingQueue = playingQueue
                )
            }
            _playingQueue.tryEmit(playingQueue)
        }
    }

    private fun collectCurrentSong() = applicationScope.launch {
        musicController.currentPlayingMusic.collectLatest { currentPlayingMusic ->
            Timber.tag(TAG).d( "collectCurrentSong - $currentPlayingMusic")

            _timePassed.update { 0L }
            _musicState.update {
                it.copy(currentPlayingMusic = currentPlayingMusic)
            }
        }
    }

    private fun collectIsPlaying() = applicationScope.launch {
        musicController.isPlaying.collectLatest { isPlaying ->
            Timber.tag(TAG).d( "collectIsPlaying - ${isPlaying}")
            if(isPlaying) {
                musicController.collectDurationFromPlayer()
            }

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
                    repeatMode = repeatMode
                )
            }
        }
    }
    private fun collectShuffleMode() = applicationScope.launch {
        musicController.shuffleMode.collectLatest { shuffleMode ->
            Timber.tag(TAG).d( "collectShuffleMode - ${shuffleMode}")
            _musicState.update {
                it.copy(
                    shuffleMode = shuffleMode
                )
            }
        }
    }

    private fun collectExoPlayerState() = applicationScope.launch {
        musicController.exoPlayerState.collectLatest { state ->
            _exoPlayerState.tryEmit(state)

            _musicState.update {
                it.copy(
                    isBuffering = state == ExoPlayer.STATE_BUFFERING
                )
            }
        }
    }

    fun onPlay(
        song: Song = musicState.value.currentPlayingMusic,
    ) = applicationScope.launch(Dispatchers.IO) {
        val state = musicState.value
        if(isFirstPlay(song)) { // 최초 실행시
            val firstSong = state.playingQueue.firstOrNull() ?: song
            musicController.play(firstSong)
            return@launch
        }

        if(state.currentPlayingMusic == song) { // pause -> play 한 경우
            musicController.resume()
            return@launch
        }

        musicController.play(song)
    }

    fun onPause() = applicationScope.launch(Dispatchers.IO) {
        musicController.pause()
    }
    fun onStop() = applicationScope.launch(Dispatchers.IO) {
        musicController.stop()
    }

    fun onNext() = applicationScope.launch(Dispatchers.IO) {
        musicController.next()
    }

    fun onPrevious() = applicationScope.launch(Dispatchers.IO) {
        musicController.previous()
    }

    fun onShuffleButtonPressed() = applicationScope.launch(Dispatchers.IO) {
        musicController.changeShuffleMode()
    }
    fun onRepeatButtonPressed() = applicationScope.launch(Dispatchers.IO) {
        musicController.changeRepeatMode()
    }
    fun onRefresh() = applicationScope.launch(Dispatchers.IO) {
        musicController.refresh()
    }
    fun onSkipDurationChanged() = applicationScope.launch(Dispatchers.IO) {
        musicController.changeSkipDuration()
    }

    fun snapTo(duration: Long) = applicationScope.launch(Dispatchers.Main) {
        musicController.snapTo(
            duration = duration,
            fromUser = true
        )
    }

    fun onPlayAtPlayingQueue(
        songs: List<Song>,
        addToPlayingQueue: Boolean,
        autoPlay: Boolean
    ) = applicationScope.launch(Dispatchers.IO){
        if(addToPlayingQueue) { // TabToSelect
            playingQueueUseCase.addToPlayingQueue(
                song = songs.toTypedArray(),
                autoPlayWhenQueueChanged = autoPlay
            )
        } else { // TabToPlay
            if(songs == musicState.value.playingQueue) {
                if(songs.isEmpty()) return@launch
                if(!autoPlay) return@launch
                musicController.play(songs.first())
            } else {
                playingQueueUseCase.openQueue(
                    song = songs.toTypedArray(),
                    autoPlayWhenQueueChanged = autoPlay
                )
            }
        }
    }

    fun onDeleteAtPlayingQueue(songs: List<Song>) = applicationScope.launch {
        playingQueueUseCase.deletePlayingQueue(*songs.toTypedArray())
    }

    private fun collectPlayingQueueFromProvider() = applicationScope.launch {
        playingQueueUseCase.playingQueue().collectLatest { playingQueue ->
            val autoPlayModel = playingQueueUseCase.getAutoPlayWhenQueueChanged()
            Timber.d("collectPlayingQueueFromProvider: ${playingQueue.size}, ${autoPlayModel}")
            musicController.updatePlayingQueue(playingQueue)

            val model = autoPlayModel ?: run {
                if(!playingQueue.contains(musicState.value.currentPlayingMusic)) {
                    musicController.stop()
                }
                return@collectLatest
            }
            val autoPlay = model.first
            val song = model.second

            musicController.play(song)
            if(!autoPlay) musicController.pause()
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

    private fun isFirstPlay(song: Song): Boolean { // 현재 재생중인 곡이 없고, 요청한 곡이 emptySong일떄 (최초 실행시)
        return song == Song.default && musicState.value.currentPlayingMusic == Song.default
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