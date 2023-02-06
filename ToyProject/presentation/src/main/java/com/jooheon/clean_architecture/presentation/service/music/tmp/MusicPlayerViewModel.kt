package com.jooheon.clean_architecture.presentation.service.music.tmp

import android.app.Activity
import android.content.*
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.base.extensions.DiName
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.service.music.extensions.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.MUSIC_DURATION
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.MUSIC_STATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named(DiName.IO) private val dispatcher: CoroutineDispatcher,
    private val musicController: MusicController
): BaseViewModel() {
    override val TAG = MusicService::class.java.simpleName + "@" + MusicPlayerViewModel::class.java.simpleName

    private var musicService: MusicService? = null
    private val connectionMap = WeakHashMap<Context, ServiceConnection>()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _navigateToPlayListScreen = Channel<Unit>()
    val navigateToPlayListScreen = _navigateToPlayListScreen.receiveAsFlow()

    private val _navigateToAodPlayer = Channel<Unit>()
    val navigateToAodPlayer = _navigateToAodPlayer.receiveAsFlow()

    private val _expandTest = MutableSharedFlow<Boolean>()
    val expandTest = _expandTest.asSharedFlow()

    private val _timePassed = MutableStateFlow(0L)
    val timePassed = _timePassed.asStateFlow()

    private val _motionFraction = MutableStateFlow(0f)
    val motionFraction = _motionFraction.asStateFlow()

    fun audioSessionId() = musicController.audioSessionId()

    init {
        Log.d(TAG, "musicController - ${musicController}")
        collectMusicState()
        collectSongList()
        collectCurrentSong()
        collectIsPlaying()
        collectDuration()
        collectRepeatMode()
        collectShuffleMode()
        collectTimePassed()
        collectSkipDuration()
    }

    private fun commandToService() {
        if(serviceIntent == null) return

        serviceIntent.putExtra(MUSIC_STATE, musicState.value)
        serviceIntent.putExtra(MUSIC_DURATION, timePassed.value)
        MusicService.startService(context, serviceIntent)
    }

    private fun collectMusicState() = viewModelScope.launch(dispatcher) {
        musicState.collectLatest {
            commandToService()
        }
    }

    private fun collectDuration() = viewModelScope.launch {
        // 손으로 Swipe했을때 호출
        musicController.currentDuration.collectLatest { currentDuration ->
            _timePassed.update { currentDuration }
        }
    }

    private fun collectTimePassed() = viewModelScope.launch {
        musicController.timePassed.collectLatest { timePassed ->
            _timePassed.update { timePassed }
        }
    }
    private fun collectSongList() = viewModelScope.launch {
        musicController.songs.collectLatest { songs ->
            _musicState.update {
                it.copy(
                    songs = songs
                )
            }
        }
    }
    private fun collectCurrentSong() = viewModelScope.launch {
        musicController.currentPlayingMusic.collectLatest { currentPlayingMusic ->
            Log.d(TAG, "collectCurrentSong - $currentPlayingMusic")
            _timePassed.update { 0L }
            _musicState.update {
                it.copy(
                    currentPlayingMusic = currentPlayingMusic
                )
            }
        }
    }
    private fun collectIsPlaying() = viewModelScope.launch {
        musicController.isPlaying.collectLatest { isPlaying ->
            _musicState.update {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }
    }
    private fun collectRepeatMode() = viewModelScope.launch {
        musicController.repeatMode.collectLatest { repeatMode ->
            _musicState.update {
                it.copy(
                    repeatMode = repeatMode
                )
            }
        }
    }
    private fun collectShuffleMode() = viewModelScope.launch {
        musicController.shuffleMode.collectLatest { shuffleMode ->
            _musicState.update {
                it.copy(
                    shuffleMode = shuffleMode
                )
            }
        }
    }

    private fun collectSkipDuration() = viewModelScope.launch {
        musicController.skipState.collectLatest { skipDuration ->
            _musicState.update {
                it.copy(
                    skipForwardBackward = skipDuration
                )
            }
        }
    }

    fun onPlayPauseButtonPressed(song: Entity.Song) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "onPlayPauseButtonPressed")
        if(musicController.isPlaying.value) {
            musicController.stop()
        } else {
            musicController.play(song)
        }
    }

    fun onNext() = viewModelScope.launch(Dispatchers.IO) {
        musicController.next()
    }

    fun onPrevious() = viewModelScope.launch(Dispatchers.IO) {
        musicController.previous()
    }

    fun onPlayListButtonPressed() = viewModelScope.launch(Dispatchers.Main) {
        _navigateToPlayListScreen.send(Unit)
    }

    fun onMusicBottomBarPressed() = viewModelScope.launch(Dispatchers.Main) {
        _navigateToAodPlayer.send(Unit)
    }

    fun onShuffleButtonPressed() = viewModelScope.launch(Dispatchers.IO) {
        musicController.changeShuffleMode()
    }
    fun onRepeatButtonPressed() = viewModelScope.launch(Dispatchers.IO) {
        musicController.changeRepeatMode()
    }

    fun onSkipDurationChanged() = viewModelScope.launch(Dispatchers.IO) {
        musicController.changeSkipDuration()
    }

    fun onExpandCollapsed() = viewModelScope.launch(Dispatchers.IO) {

        _expandTest.emit(!expandTest.last())
    }

    fun snapTo(duration: Long) = viewModelScope.launch(Dispatchers.Main) {
        musicController.snapTo(
            duration = duration,
            fromUser = true
        )
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

    override fun onCleared() {
        super.onCleared()
//        musicPlayerRemote.unsubscribe(MEDIA_ID_ROOT)
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