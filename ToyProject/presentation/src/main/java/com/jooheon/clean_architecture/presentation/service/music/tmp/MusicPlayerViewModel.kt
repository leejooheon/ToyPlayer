package com.jooheon.clean_architecture.presentation.service.music.tmp

import android.app.Activity
import android.content.*
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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

    private val _navigateToPlayListScreen = MutableSharedFlow<Boolean>()
    val navigateToPlayListScreen = _navigateToPlayListScreen.asSharedFlow()

    private val _timePassed = MutableStateFlow(0L)
    val timePassed = _timePassed.asStateFlow()

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
        musicController.currentDuration.collectLatest { // 손으로 Swipe했을때 호출
            _timePassed.value = it
        }
    }

    private fun collectTimePassed() = viewModelScope.launch {
        musicController.timePassed.collectLatest {
            _timePassed.value = it
        }
    }

    private fun collectSongList() = viewModelScope.launch {
        musicController.songs.collectLatest {
            _musicState.value = musicState.value.copy(songs = it)
        }
    }
    private fun collectCurrentSong() = viewModelScope.launch {
        musicController.currentPlayingMusic.collectLatest {
            Log.d(TAG, "collectCurrentSong - $it")
            _timePassed.value = 0L
            _musicState.value = musicState.value.copy(currentPlayingMusic = it)
        }
    }
    private fun collectIsPlaying() = viewModelScope.launch {
        musicController.isPlaying.collectLatest {
            _musicState.value = musicState.value.copy(isPlaying = it)
        }
    }
    private fun collectRepeatMode() = viewModelScope.launch {
        musicController.repeatMode.collectLatest {
            _musicState.value = musicState.value.copy(repeatMode = it)
        }
    }
    private fun collectShuffleMode() = viewModelScope.launch {
        musicController.shuffleMode.collectLatest {
            _musicState.value = musicState.value.copy(shuffleMode = it)
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

    fun onPlayListButtonPressed() = viewModelScope.launch(Dispatchers.Main) {
        _navigateToPlayListScreen.emit(true)
    }

    fun onMusicBottomBarPressed(song: Entity.Song) = viewModelScope.launch(Dispatchers.Main) {
        Log.d(TAG, "onMusicBottomBarPressed")
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