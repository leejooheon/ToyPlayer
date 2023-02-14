package com.jooheon.clean_architecture.presentation.service.music.tmp.notification

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicControllerUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MediaSessionCallback(
    private val serviceScope: CoroutineScope,
    private val musicControllerUseCase: MusicControllerUseCase
) : MediaSessionCompat.Callback() {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaSessionCallback::class.java.simpleName

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        Log.d(TAG, "onCustomAction - $action")

        serviceScope.launch {
            when (action) {
                CYCLE_REPEAT -> musicControllerUseCase.onRepeatButtonPressed()
                TOGGLE_SHUFFLE -> musicControllerUseCase.onShuffleButtonPressed()
            }
        }
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        Log.d(TAG, "onPlayFromMediaId - $mediaId")
        // MediaControllerTest 앱에서 Play 버튼 누를 시 호출된다.

        serviceScope.launch {
            val musicState = musicControllerUseCase.musicState.value
            val song = musicState.songs.firstOrNull {
                it.id == mediaId?.toLongOrNull()
            } ?: return@launch

            musicControllerUseCase.onPlayPauseButtonPressed(song)
        }
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        Log.d(TAG, "onPlayFromUri - ${uri}")

        serviceScope.launch {
            musicControllerUseCase.play(uri)
        }
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        Log.d(TAG, "onPlayFromSearch - ${query}")

        if(query == null) return

        serviceScope.launch {
            val musicState = musicControllerUseCase.musicState.value
            val song = musicState.songs.firstOrNull {
                it.title.contains(query, true)
            } ?: return@launch

            musicControllerUseCase.onPlayPauseButtonPressed(song)
        }
    }

    override fun onPrepare() {
        super.onPrepare()
        Log.d(TAG, "onPrepare")
    }

    override fun onPlay() {
        super.onPlay()
        Log.d(TAG, "onPlay")

        serviceScope.launch {
            val musicState = musicControllerUseCase.musicState.value
            musicControllerUseCase.onPlayPauseButtonPressed(musicState.currentPlayingMusic)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")

        serviceScope.launch {
            musicControllerUseCase.onStop()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

        serviceScope.launch {
            musicControllerUseCase.onPause()
        }
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        Log.d(TAG, "onSkipToNext")

        serviceScope.launch {
            musicControllerUseCase.onNext()
        }
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        Log.d(TAG, "onSkipToPrevious")

        serviceScope.launch {
            musicControllerUseCase.onPrevious()
        }
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        Log.d(TAG, "onSeekTo - $pos")

        serviceScope.launch {
            musicControllerUseCase.snapTo(
                duration = pos
            )
        }
    }

    companion object {
        const val TOYPROJECT_PACKAGE_NAME = "com.jooheon.toyproject"
        const val ACTION_QUIT = "$TOYPROJECT_PACKAGE_NAME.quitservice"
        const val CYCLE_REPEAT = "$TOYPROJECT_PACKAGE_NAME.cyclerepeat"
        const val TOGGLE_SHUFFLE = "$TOYPROJECT_PACKAGE_NAME.toggleshuffle"
        const val TOGGLE_FAVORITE = "$TOYPROJECT_PACKAGE_NAME.togglefavorite"


        const val MEDIA_SESSION_ACTIONS = (
                PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SEEK_TO
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_REWIND
                or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
            )
    }
}