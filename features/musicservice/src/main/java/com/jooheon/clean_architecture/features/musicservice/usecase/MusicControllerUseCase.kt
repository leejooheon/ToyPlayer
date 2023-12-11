package com.jooheon.clean_architecture.features.musicservice.usecase

import android.media.session.PlaybackState
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.musicservice.ext.enqueue
import com.jooheon.clean_architecture.features.musicservice.ext.forceEnqueue
import com.jooheon.clean_architecture.features.musicservice.ext.forceSeekToNext
import com.jooheon.clean_architecture.features.musicservice.ext.forceSeekToPrevious
import com.jooheon.clean_architecture.features.musicservice.ext.playAtIndex
import com.jooheon.clean_architecture.features.musicservice.ext.playbackErrorReason
import com.jooheon.clean_architecture.features.musicservice.ext.shuffledItems
import com.jooheon.clean_architecture.features.musicservice.ext.toMediaItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import timber.log.Timber

class MusicControllerUseCase(
    private val applicationScope: CoroutineScope,
    private val playingQueueUseCase: PlayingQueueUseCase,
    private val musicStateHolder: MusicStateHolder,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + MusicControllerUseCase::class.java.simpleName
    private val immediate = Dispatchers.Main.immediate
    private var player: Player? = null

    private val _songLibrary = MutableStateFlow(emptyList<Song>())

    private val _playingQueue = MutableStateFlow<List<Song>>(emptyList())
    val playingQueue = _playingQueue.asStateFlow()

    private val _musicState = MutableStateFlow(MusicState())
    val musicState = _musicState.asStateFlow()

    private val _timePassed = MutableStateFlow(0L)
    val timePassed = _timePassed.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.STATE_NONE)
    val playbackState = _playbackState.asStateFlow()

    private val _musicStreamErrorChannel = Channel<Resource.Failure>()
    val musicStreamErrorChannel = _musicStreamErrorChannel.receiveAsFlow()

    init {
        collectMediaItems()
        collectCurrentWindow()
        collectIsPlaying()
        collectDuration()
        collectRepeatMode()
        collectShuffleMode()
        collectPlaybackState()
        collectPlayerState()
        collectPlaybackError()
    }

    fun setPlayer(player: Player) {
        this.player = player
        initPlayingQueue()
        initPlaybackOptions()
    }

    private fun collectDuration() = applicationScope.launch {
        musicStateHolder.currentDuration.collectLatest { currentDuration ->
            _timePassed.update { currentDuration }
        }
    }
    private fun collectMediaItems() = applicationScope.launch {
        musicStateHolder.mediaItems.collectLatest { mediaItems ->
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
            musicStateHolder.currentWindow
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

    private fun collectIsPlaying() = applicationScope.launch {
        musicStateHolder.isPlaying.collectLatest { isPlaying ->
            Timber.tag(TAG).d( "collectIsPlaying - $isPlaying")
            _musicState.update {
                it.copy(
                    isPlaying = isPlaying
                )
            }
        }
    }
    private fun collectRepeatMode() = applicationScope.launch {
        musicStateHolder.repeatMode.collectLatest { repeatMode ->
            Timber.tag(TAG).d( "collectRepeatMode - $repeatMode")
            _musicState.update {
                it.copy(
                    repeatMode = RepeatMode.getByValue(repeatMode)
                )
            }
        }
    }
    private fun collectShuffleMode() = applicationScope.launch {
        musicStateHolder.shuffleMode.collectLatest { shuffleMode ->
            Timber.tag(TAG).d( "collectShuffleMode - $shuffleMode")
            _musicState.update {
                it.copy(
                    shuffleMode = ShuffleMode.getByValue(shuffleMode)
                )
            }

            shuffle(
                playWhenReady = musicState.value.isPlaying
            )
        }
    }
    private fun collectPlayerState() = applicationScope.launch {
        musicStateHolder.playerState.collectLatest { state ->
            if(state == Player.STATE_READY) {
                updateCurrentPlayingMusic(musicState.value.currentPlayingMusic)
            }
        }
    }
    private fun collectPlaybackState() = applicationScope.launch {
        musicStateHolder.playbackState.collectLatest { state ->
            _playbackState.tryEmit(state)
            _musicState.update {
                it.copy(
                    isBuffering = state == PlaybackState.STATE_BUFFERING
                )
            }
        }
    }
    private fun collectPlaybackError() = applicationScope.launch {
        musicStateHolder.playbackError.collectLatest { error ->
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

    fun enqueue(
        song: Song,
        playWhenReady: Boolean
    ) = applicationScope.launch(immediate) {
        val player = player ?: return@launch

        val songLibrary = _songLibrary.value.toMutableList()
        val index = playingQueue.value.indexOfFirst {
            it.id() == song.id()
        }

        if(index != C.INDEX_UNSET) {
            player.removeMediaItem(index)
            songLibrary.remove(song) // remove duplicate
        }
        songLibrary.add(song)
        _songLibrary.update { songLibrary }

        player.enqueue(
            mediaItem = song.toMediaItem(),
            playWhenReady = playWhenReady
        )
    }

    fun enqueue(
        songs: List<Song>,
        addNext: Boolean,
        playWhenReady: Boolean
    ) = applicationScope.launch(immediate) {
        val player = player ?: return@launch

        if (addNext) { // TabToSelect
            val songLibrary = _songLibrary.value.toMutableList()

            val newMediaItems = songs.filter { song ->
                songLibrary.none { it.id() == song.id() } // remove duplicate
            }.also {
                songLibrary.addAll(it)
                _songLibrary.update { songLibrary }
            }.map {
                it.toMediaItem()
            }

            player.enqueue(
                mediaItems = newMediaItems,
                playWhenReady = playWhenReady
            )
        } else { // TabToPlay
            _songLibrary.update { songs }
            val newMediaItems = songs.map { it.toMediaItem() }

            player.forceEnqueue(
                mediaItems = newMediaItems,
                playWhenReady = playWhenReady
            )
        }
    }

    fun onDeleteAtPlayingQueue(songs: List<Song>) = applicationScope.launch(immediate) {
        val playingQueue = _playingQueue.value

        val songIndexList = songs.filter {
            it != Song.default
        }.map { targetSong ->
            playingQueue.indexOfFirst { it.id() == targetSong.id() }
        }.filter {
            it != -1
        }

        songIndexList.forEach {
            player?.removeMediaItem(it)
        }
    }

    fun onPlay(
        song: Song = musicState.value.currentPlayingMusic,
    ) = applicationScope.launch(immediate) {
        val state = musicState.value

        val index = state.playingQueue.indexOfFirst {
            it.id() == song.id()
        }

        if(index != C.INDEX_UNSET) {
            player?.playAtIndex(index, C.TIME_UNSET)
        } else {
            enqueue(
                song = song,
                playWhenReady = true
            )
        }
    }
    fun shuffle(playWhenReady: Boolean) = applicationScope.launch(immediate) {
        val player = player ?: return@launch
        val shuffledItems = player.shuffledItems()

        player.forceEnqueue(
            mediaItems = shuffledItems,
            playWhenReady = playWhenReady
        )
    }
    fun onPause() = applicationScope.launch(immediate) {
        player?.pause()
    }
    fun onStop() = applicationScope.launch(immediate) {
        player?.stop()
    }
    fun onNext() = applicationScope.launch(immediate) {
        player?.forceSeekToNext()
    }
    fun onPrevious() = applicationScope.launch(immediate) {
        player?.forceSeekToPrevious()
    }
    fun snapTo(duration: Long) = applicationScope.launch(immediate) {
        player?.seekTo(duration)
    }

    fun onShuffleButtonPressed() = applicationScope.launch(immediate) {
        val state = musicState.value
        val shuffleModeEnabled = when(state.shuffleMode) {
            ShuffleMode.SHUFFLE -> false
            ShuffleMode.NONE -> true
        }
        player?.shuffleModeEnabled = shuffleModeEnabled
    }
    fun onRepeatButtonPressed() = applicationScope.launch(immediate) {
        val state = musicState.value
        val repeatMode = when(state.repeatMode) {
            RepeatMode.REPEAT_ALL -> RepeatMode.REPEAT_ONE
            RepeatMode.REPEAT_ONE -> RepeatMode.REPEAT_OFF
            RepeatMode.REPEAT_OFF -> RepeatMode.REPEAT_ALL
        }
        player?.repeatMode = repeatMode.ordinal
    }

    fun release() {
        player = null
    }

    private fun updateCurrentPlayingMusic(song: Song) {
        Timber.tag(TAG).d("updateCurrentPlayingMusic: ${song.title}, Id: ${song.id()}")

        _timePassed.update { 0L }
        _musicState.update {
            it.copy(currentPlayingMusic = song)
        }
    }

    private fun initPlayingQueue() = applicationScope.launch(Dispatchers.IO) {
        playingQueueUseCase.getPlayingQueue().onEach {
            if(it is Resource.Success) {
                val playingQueue = it.value
                _songLibrary.tryEmit(playingQueue)
                val newMediaItems = playingQueue.map { it.toMediaItem() }
                withContext(immediate) {
                    player?.enqueue(
                        mediaItems = newMediaItems,
                        playWhenReady = false
                    )
                }

//                playingQueueUseCase.getPlayingQueuePosition(), // TODO: set last position
            }
        }.launchIn(this)
    }

    private fun initPlaybackOptions() = applicationScope.launch(immediate) {
        val repeatMode = playingQueueUseCase.repeatMode()
        val shuffleMode = playingQueueUseCase.shuffleMode()

        player?.repeatMode = repeatMode.ordinal
        player?.shuffleModeEnabled = shuffleMode == ShuffleMode.SHUFFLE
    }
}