package com.jooheon.clean_architecture.presentation.service.music

import android.content.*
import android.media.MediaMetadata
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.extensions.*
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.utils.MusicUtil.print
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MusicPlayerRemote @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicPlayerUseCase: MusicPlayerUseCase,
    private val isPreview: Boolean = false
) {
    private val TAG = "Remote" + MusicService::class.java.simpleName
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaBrowser: MediaBrowserCompat

    init {
        if(!isPreview) {
            mediaBrowser = MediaBrowserCompat(
                context,
                ComponentName(context, MusicService::class.java),
                ConnectionCallback(), null
            ).apply {
                connect()
            }
        }
    }

    private val _songList = MutableStateFlow<MutableList<Entity.Song>?>(null)
    val songList = _songList.asStateFlow()

    private val _playbackState = MutableStateFlow<PlaybackStateCompat?>(null)
    val playbackState = _playbackState.asStateFlow()

    private val _currentSong = MutableStateFlow<MediaMetadataCompat?>(null)
    val currentSong = _currentSong.asStateFlow()

    val timePassed = flow {
        while (true) {
            val duration = playbackState.value?.currentPlaybackPosition
                ?: 0
//            if (uiState.value.musicSliderState.timePassed != duration)
            emit(duration)
            delay(1000L)
        }
    }

    private val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls


    suspend fun subscribeToService(): Resource<List<MediaBrowserCompat.MediaItem>> =
        suspendCoroutine {
            subscribe(
                MusicService.MEDIA_ID_ROOT,
                object : MediaBrowserCompat.SubscriptionCallback() {
                    override fun onChildrenLoaded(
                        parentId: String,
                        children: MutableList<MediaBrowserCompat.MediaItem>
                    ) {
                        super.onChildrenLoaded(parentId, children)
                        Log.d(TAG,"children loaded $children")
                        it.resume(Resource.Success(children))
                    }

                    override fun onError(parentId: String) {
                        super.onError(parentId)
                        it.resume(
                            Resource.Failure(
                                failureStatus = FailureStatus.EMPTY,
                                message = "Failed to subscribe"
                            )
                        )
                    }
                }
            )
        }

    private fun subscribe(parentId: String, callbacks: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.subscribe(parentId, callbacks)
    }

    fun unsubscribe(parentId: String) {
        mediaBrowser.unsubscribe(parentId)
    }

    fun updateSongList() {
        emit {
            Log.d("asd", "singleton test - remote ${musicPlayerUseCase}")
            _songList.value = musicPlayerUseCase.allMusic.toMutableList()
        }
    }

    fun openQueue(queue: List<Entity.Song>) {
        Log.d(TAG, "openQueue: ${queue.first()}")

        playFromMediaId(queue.first().id.toString())
    }

    fun playPause(songId: String, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && songId == currentSong.value?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) {
            playPauseCurrentSong(toggle)
        } else {
            playFromMediaId(songId)
        }
    }
    fun stopPlaying() = transportControls.stop()

    fun seekTo(pos: Long) = transportControls.seekTo(pos)

    fun pause() = transportControls.pause()

    fun play(){ transportControls.play() }

    fun fastForward() = transportControls.fastForward()

    fun rewind() = transportControls.rewind()

    fun skipToNextTrack() = transportControls.skipToNext()

    fun skipToPrev() = transportControls.skipToPrevious()

    private fun playFromMediaId(mediaId: String) = transportControls.playFromMediaId(mediaId, null)

    private fun playPauseCurrentSong(toggle: Boolean) {
        playbackState.value?.let {
            when {
                it.isPlaying -> if (toggle) transportControls.pause()
                it.isPlayEnabled -> transportControls.play()
                else -> Unit
            }
        }
    }

    private inner class ConnectionCallback: MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            super.onConnected()
            Log.d(TAG, "onConnected")
            mediaController =  MediaControllerCompat(
                context, mediaBrowser.sessionToken
            ).apply {
                registerCallback(MediaControllerCallback())
            }
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            Log.d(TAG, "onConnectionSuspended")
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            Log.d(TAG, "onConnectionFailed")
        }
    }

    private inner class MediaControllerCallback: MediaControllerCompat.Callback() {

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            Log.d(TAG, "onSessionDestroyed")
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            Log.d(TAG, "onSessionEvent")
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            Log.d(TAG, "onPlaybackStateChanged - ${state?.state ?: "error"}")
            emit { _playbackState.emit(state) }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            emit {
                val data = if (metadata?.id == null) {
                    NOTHING_PLAYING
                } else {
                    metadata
                }

                _currentSong.emit(data)
            }
        }
    }

    private fun emit(emission: suspend () -> Unit) = coroutineScope.launch {
        emission()
    }
}