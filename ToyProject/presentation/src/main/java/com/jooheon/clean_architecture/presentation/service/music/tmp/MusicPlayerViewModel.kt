package com.jooheon.clean_architecture.presentation.service.music.tmp

import android.app.Activity
import android.content.*
import android.os.Build
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
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named(DiName.IO) private val dispatcher: CoroutineDispatcher,
    private val musicController: MusicController
): BaseViewModel() {
    override val TAG = MusicPlayerViewModel::class.java.simpleName

    private val connectionMap = WeakHashMap<Context, ServiceConnection>()
    private var musicService: MusicService? = null

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _navigateToPlayListScreen = MutableSharedFlow<Boolean>()
    val navigateToPlayListScreen = _navigateToPlayListScreen.asSharedFlow()

    init {
        Log.d(TAG, "musicController - ${musicController}")
        collectMusicState()
        collectSongList()
        collectCurrentSong()
        collectIsPlaying()
        collectDuration()
    }

    private fun collectMusicState() {
        viewModelScope.launch(dispatcher) {
            musicState.collectLatest { state ->
                if(serviceIntent == null) return@collectLatest

                serviceIntent.putExtra("MusicService", state) // FIXME: name 디파인해서 쓰자

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else context.startService(serviceIntent)
            }
        }
    }

    private fun collectSongList() = viewModelScope.launch {
        musicController.songs.collectLatest {
            _musicState.value = musicState.value.copy(songs = it)
        }
    }

    private fun collectCurrentSong() = viewModelScope.launch {
        musicController.currentPlayingMusic.collectLatest {
            _musicState.value = musicState.value.copy(currentPlayingMusic = it)
        }
    }

    private fun collectIsPlaying() = viewModelScope.launch {
        musicController.isPlaying.collectLatest {
            _musicState.value = musicState.value.copy(isPlaying = it)
        }
    }
    private fun collectDuration() = viewModelScope.launch {
        musicController.currentDuration.collectLatest {
            _musicState.value = musicState.value.copy(currentDuration = it)
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

    /**
    init {
        initMusicService()
        collectTimePassed()
        collectCurrentSong()
        collectPlaybackState()
        collectSongList()
    }

    private fun collectTimePassed() = viewModelScope.launch {
        musicPlayerRemote.timePassed.collectLatest {
            Log.d(TAG, "collectTimePassed - $it")
        }
    }

    private fun collectCurrentSong() = viewModelScope.launch {
        musicPlayerRemote.currentSong.collectLatest {
            val mediaMeta = it ?: return@collectLatest
            musicPlayerRemote.songList.value?.firstOrNull { mediaMeta.mediaId() == it.id } ?.let {
                _uiState.value = uiState.value.copy(currentPlayingMusic = it)
            }
        }
    }

    private fun collectPlaybackState() = viewModelScope.launch {
        musicPlayerRemote.playbackState.collectLatest {
            val musicState = it?.getMusicState() ?: MusicState.NONE
            Log.d(TAG, "collectPlaybackState - ${musicState}")
            _uiState.value = uiState.value.copy(musicState = musicState)
        }
    }

    private fun collectSongList() = viewModelScope.launch {
        musicPlayerRemote.songList.collectLatest {
            val songList = it ?: return@collectLatest
            _uiState.value = uiState.value.copy(songList = songList)
        }
    }

    private fun initMusicService() = viewModelScope.launch {
        val resource = musicPlayerRemote.subscribeToService()
        if(resource is Resource.Success) {
            Log.d(TAG, "onChildrenLoaded - ${resource.value.size}")
            musicPlayerRemote.updateSongList()
        } else {
            Log.d(TAG, (resource as? Resource.Failure)?.message ?: "Failure")
        }
    }
    **/

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