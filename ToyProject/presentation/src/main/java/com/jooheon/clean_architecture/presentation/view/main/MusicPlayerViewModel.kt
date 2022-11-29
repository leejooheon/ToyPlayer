package com.jooheon.clean_architecture.presentation.view.main

import android.media.MediaMetadata
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.MEDIA_ID_ROOT
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicScreenState
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicState
import com.jooheon.clean_architecture.presentation.service.music.extensions.getMusicState
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    private val musicPlayerRemote: MusicPlayerRemote
): BaseViewModel() {
    override val TAG = MusicPlayerViewModel::class.java.simpleName

    private val _uiState = mutableStateOf(MusicScreenState())
    val uiState: State<MusicScreenState> = _uiState

    init {
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
            (mediaMeta.mediaMetadata as? MediaMetadata) ?.let {
                Log.d("JH", "r: albumId - ${it.getString(MediaMetadata.METADATA_KEY_ARTIST)}")
                Log.d("JH", "r: albumName - ${it.getString(MediaMetadata.METADATA_KEY_AUTHOR)}")

                val song = MusicUtil.parseSongFromMediaMetadata(it)
                _uiState.value = uiState.value.copy(currentPlayingMusic = song)
            } ?: run {
                Log.d(TAG, "collectCurrentSong - null")
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

    fun updateSongList(songList: List<Entity.Song>) {
        musicPlayerRemote.updateSongList(songList)
    }

    fun skipToNextTrack() = musicPlayerRemote.skipToNextTrack()

    fun skipToPrevTrack() = musicPlayerRemote.skipToPrev()

    fun seekTo(pos: Long) = musicPlayerRemote.seekTo(pos)

    fun fastForward() = musicPlayerRemote.fastForward()

    fun rewind() = musicPlayerRemote.rewind()

    fun stopPlaying() = musicPlayerRemote.stopPlaying()

    fun playFromMediaId(mediaId: String) = musicPlayerRemote.playPause(mediaId)

    fun onPlayPauseButtonPressed(song: Entity.Song) = viewModelScope.launch(Dispatchers.Main) {
        musicPlayerRemote.playPause(song.id.toString(), true)
    }

    fun onMusicBottomBarPressed(song: Entity.Song) = viewModelScope.launch(Dispatchers.Main) {
        Log.d(TAG, "onMusicBottomBarPressed")
    }

    override fun onCleared() {
        super.onCleared()
        musicPlayerRemote.unsubscribe(MEDIA_ID_ROOT)
    }

    suspend fun subscribeToService(): Resource<List<MediaBrowserCompat.MediaItem>> =
        suspendCoroutine {
            musicPlayerRemote.subscribe(
                MEDIA_ID_ROOT,
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
}