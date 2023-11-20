package com.jooheon.clean_architecture.features.musicservice.usecase

import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.ext.playbackErrorReason
import com.jooheon.clean_architecture.features.musicservice.ext.toMediaItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import timber.log.Timber

@UnstableApi
class MusicControllerUsecase(
    private val applicationScope: CoroutineScope,
    private val musicController: MusicController,
    private val playingQueueUseCase: PlayingQueueUseCase,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUsecase::class.java.simpleName

    private val _songLibrary = MutableStateFlow(emptyList<Song>())

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
        collectPlayerInitialized()
        collectMediaItems()
        collectCurrentWindow()
        collectMediaItemIndex()
        collectIsPlaying()
        collectDuration()
        collectRepeatMode()
        collectShuffleMode()
        collectExoPlayerState()
        collectPlaybackException()
    }

    private fun collectPlayerInitialized() = applicationScope.launch {
        musicController.playerInitialized.collectLatest {
            initPlayingQueue()
            initPlaybackOptions()
        }
    }

    private fun collectDuration() = applicationScope.launch {
        musicController.currentDuration.collectLatest { currentDuration ->
            _timePassed.update { currentDuration }
        }
    }
    private fun collectMediaItems() = applicationScope.launch {
        musicController.mediaItems.collectLatest { mediaItems ->
            Timber.tag(TAG).d( "collectMediaItems: ${mediaItems.size}")
            val originPlaylist = _songLibrary.value

            val newPlayingQueue = mediaItems.mapNotNull { mediaItem ->
                originPlaylist.firstOrNull { it.id() == mediaItem.mediaId }
            }

            playingQueueUseCase.updatePlayingQueue(song = newPlayingQueue.toTypedArray())
            _playingQueue.tryEmit(newPlayingQueue)
            _musicState.update {
                it.copy(playingQueue = newPlayingQueue)
            }
        }
    }
    private fun collectCurrentWindow() = applicationScope.launch {
        combine(
            playingQueue,
            musicController.currentWindow
        ) { playingQueue, currentWindow ->
            Pair(playingQueue, currentWindow)
        }.collectLatest { (playingQueue, currentWindow) ->
            currentWindow ?: return@collectLatest
            val song = playingQueue.firstOrNull { it.id() == currentWindow.mediaItem.mediaId } ?: Song.default
            Timber.tag(TAG).d( "collectCurrentWindow: ${song.title.defaultEmpty()}")
            if(song == Song.default) return@collectLatest

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
            mediaItemsIndices = songIndexList,
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

    fun releaseMediaBrowser() {
        musicController.releaseMediaBrowser()
    }

    private fun updateCurrentPlayingMusic(song: Song) {
        Timber.tag(TAG).d("updateCurrentPlayingMusic: ${song.title}, Id: ${song.id()}")

        _timePassed.update { 0L }
        _musicState.update {
            it.copy(currentPlayingMusic = song)
        }
    }
    private suspend fun initPlayingQueue() = withContext(Dispatchers.IO) {
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

    private suspend fun initPlaybackOptions() {
        val repeatMode = playingQueueUseCase.repeatMode()
        val shuffleMode = playingQueueUseCase.shuffleMode()

        musicController.changeRepeatMode(repeatMode.ordinal)
        musicController.changeShuffleMode(shuffleMode == ShuffleMode.SHUFFLE)
    }
}