package com.jooheon.clean_architecture.features.musicservice.usecase

import android.app.Activity
import android.content.*
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.MusicService.Companion.MUSIC_DURATION
import com.jooheon.clean_architecture.features.musicservice.MusicService.Companion.MUSIC_STATE
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.data.exoPlayerStateAsString
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
    private val TAG_PLAYER = MusicService::class.java.simpleName + "@" + "PlayerListener"
    private var musicService: MusicService? = null
    private val connectionMap = WeakHashMap<Context, ServiceConnection>()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _timePassed = MutableStateFlow(0L)
    val timePassed = _timePassed.asStateFlow()

    private val _exoPlayerState = MutableStateFlow(ExoPlayer.STATE_IDLE)
    val exoPlayerState = _exoPlayerState.asStateFlow()


    init {
        Timber.tag(TAG).d( "musicController - ${musicController}")
        collectMusicState()
        collectDuration()
        collectRepeatMode()
        collectShuffleMode()
        initPlayingQueue()
        musicController.registerPlayerListener(playerListener())
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

    fun onPlayAtPlayingQueue(
        songs: List<Song>,
        addToPlayingQueue: Boolean,
        autoPlay: Boolean
    ) = applicationScope.launch(Dispatchers.IO) {
        if (addToPlayingQueue) { // TabToSelect
            val position = musicController.songLibrary.value.size
            musicController.addToPlayingQueue(
                songs = songs,
                position = position,
            )
            if (autoPlay && songs.isNotEmpty()) {
                musicController.play(position)
            }
        } else { // TabToPlay
            musicController.openPlayingQueue(
                songs = songs,
                startIndex = 0
            )
            if (autoPlay && songs.isNotEmpty()) {
                musicController.play(0)
            }
        }
    }

    fun onDeleteAtPlayingQueue(songs: List<Song>) = applicationScope.launch(Dispatchers.IO) {
        val playingQueue = musicController.songLibrary.value

        val songIndexList = songs.filter {
            it != Song.default
        }.map { targetSong ->
            playingQueue.indexOfFirst { it.id() == targetSong.id() }
        }.filter {
            it != -1
        }

        val deletedSongs = songIndexList.map { playingQueue[it] }
        musicController.deleteFromPlayingQueue(
            songIndexList = songIndexList,
            deletedSongs = deletedSongs
        )
    }

    fun onPlay(
        song: Song = musicState.value.currentPlayingMusic,
    ) = applicationScope.launch(Dispatchers.IO) {
        val state = musicState.value
        if (isFirstPlay(song)) { // 최초 실행시
            musicController.play(0)
            return@launch
        }

        if (state.currentPlayingMusic == song) { // pause -> play 한 경우
            musicController.resume()
            return@launch
        }

        val index = state.playingQueue.indexOfFirst {
            it.id() == song.id()
        }

        musicController.play(index)
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
    fun onSkipDurationChanged() = applicationScope.launch(Dispatchers.IO) {
        musicController.changeSkipDuration()
    }

    private fun playerListener() = object: Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            Timber.tag(TAG_PLAYER).d(playbackState.exoPlayerStateAsString())

            if (playbackState == ExoPlayer.STATE_ENDED) {
                val repeatMode = musicState.value.repeatMode
                when (repeatMode) {
                    RepeatMode.REPEAT_ALL -> this@MusicControllerUsecase.onNext()
                    RepeatMode.REPEAT_OFF -> this@MusicControllerUsecase.onStop()
                    RepeatMode.REPEAT_ONE -> this@MusicControllerUsecase.onPlay()
                }
            }

            _exoPlayerState.tryEmit(playbackState)
            _musicState.update {
                it.copy(isBuffering = (playbackState == ExoPlayer.STATE_BUFFERING))
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            Timber.tag(TAG_PLAYER).d("onPlayWhenReadyChanged: ${playWhenReady}, reason: ${reason}")
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            Timber.tag(TAG_PLAYER).d( "onIsPlayingChanged - ${isPlaying}")

            if(isPlaying) {
                musicController.collectDuration()
            }

            _musicState.update {
                it.copy(isPlaying = isPlaying)
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            Timber.tag(TAG_PLAYER).d("onMediaItemTransition: ${mediaItem?.mediaMetadata?.title}")

            val playingQueue = musicController.songLibrary.value
            val id = mediaItem?.mediaId ?: run {
                Timber.tag(TAG_PLAYER).d("onMediaItemTransition: invalid mediaId")
                return
            }
            val song = playingQueue.firstOrNull {
                it.id() == id
            } ?: run {
                Timber.tag(TAG_PLAYER).d("onMediaItemTransition: not inside playingQueue ")
                return
            }

            _timePassed.update { 0L }
            _musicState.update {
                it.copy(currentPlayingMusic = song)
            }
        }

        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)
            if (reason == Player.TIMELINE_CHANGE_REASON_PLAYLIST_CHANGED) {
                applicationScope.launch(Dispatchers.Main) {
                    val playingQueue = musicController.getPlayingQueue()
                    val position = musicController.mediaItemPosition()
                    Timber.tag(TAG_PLAYER).d( "onTimelineChanged - ${playingQueue.size}, pos: ${position}")

                    playingQueueUseCase.openPlayingQueue(
                        song = playingQueue.toTypedArray()
                    )
                    _musicState.update {
                        it.copy(playingQueue = playingQueue)
                    }
                    playingQueueUseCase.setPlayingQueuePosition(position)
                }
            }
        }

        @UnstableApi
        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            applicationScope.launch(Dispatchers.Main) {
                val position = musicController.mediaItemPosition()
                Timber.tag(TAG_PLAYER).d("onMediaMetadataChanged: ${mediaMetadata.title}, position: $position")
                playingQueueUseCase.setPlayingQueuePosition(position)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            onRepeatButtonPressed(RepeatMode.values()[repeatMode])
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            onShuffleButtonPressed(shuffleModeEnabled)
        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            Timber.tag(TAG_PLAYER).d("onTracksChanged: ${tracks.groups.size}")
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Timber.tag(TAG_PLAYER).d("Jooheon onPlayerError: ${error.message}")

            _musicState.update {
                it.copy(currentPlayingMusic = Song.default)
            }
        }
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
//            if(events.size() > 1) {
//                var msg = ""
//                repeat(events.size()) { msg += "${it}th: event: ${events.get(it)}\n" }
//                Timber.tag(TAG_PLAYER).d("====== onEvents ======\n${msg}")
//            } else {
//                Timber.tag(TAG_PLAYER).d("onEvents: ${events.get(0)}")
//            }
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
        playingQueueUseCase.getPlayingQueue().onEach {
            if(it is Resource.Success) {
                musicController.openPlayingQueue(
                    songs = it.value,
                    startIndex = playingQueueUseCase.getPlayingQueuePosition()
                )
            }
        }.launchIn(this)
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