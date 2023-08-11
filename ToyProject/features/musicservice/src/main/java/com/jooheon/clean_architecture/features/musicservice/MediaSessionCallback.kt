package com.jooheon.clean_architecture.features.musicservice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.features.musicservice.data.uri
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import com.jooheon.clean_architecture.toyproject.features.musicservice.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MediaSessionCallback @Inject constructor(
    private val applicationScope: CoroutineScope,
    private val musicControllerUsecase: MusicControllerUsecase
) : MediaSessionCompat.Callback() {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaSessionCallback::class.java.simpleName

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        Timber.tag(TAG).d( "onCustomAction - $action")

        applicationScope.launch {
            when (action) {
                CYCLE_REPEAT -> musicControllerUsecase.onRepeatButtonPressed()
                TOGGLE_SHUFFLE -> musicControllerUsecase.onShuffleButtonPressed()
                ACTION_REFRESH -> musicControllerUsecase.onRefresh()
            }
        }
    }

    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        Timber.tag(TAG).d( "onMediaButtonEvent: ${mediaButtonEvent?.action.defaultEmpty()}")
        return super.onMediaButtonEvent(mediaButtonEvent)
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)

        applicationScope.launch {
            Timber.tag(TAG).d( "onPlayFromMediaId: $mediaId")

            val musicState = musicControllerUsecase.musicState.value
            val song = musicState.playingQueue.firstOrNull {
                it.audioId.toString() == mediaId.defaultEmpty()
            } ?: run {
                Timber.tag(TAG).d( "onPlayFromMediaId: not found - ${musicState.playingQueue}")
                return@launch
            }

            if(musicState.isPlaying) {
                musicControllerUsecase.onPlay(
                    song = song,
                    addToPlayingQueue = false
                )
            } else {
                musicControllerUsecase.onPause()
            }
        }
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)

        applicationScope.launch {
            Timber.tag(TAG).d( "onPlayFromUri - ${uri}")
            val musicState = musicControllerUsecase.musicState.value
            val song = musicState.playingQueue.firstOrNull { uri == it.uri } ?: return@launch
            musicControllerUsecase.onPlay(
                song = song,
                addToPlayingQueue = false
            )
        }
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        Timber.tag(TAG).d( "onPlayFromSearch - ${query}")

        if(query == null) return

        applicationScope.launch {
            val musicState = musicControllerUsecase.musicState.value
            val song = musicState.playingQueue.firstOrNull {
                it.title.contains(query, true)
            } ?: return@launch

            if(musicState.isPlaying) {
                musicControllerUsecase.onPlay(
                    song = song,
                    addToPlayingQueue = false,
                )
            } else {
                musicControllerUsecase.onPause()
            }
        }
    }

    override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPrepareFromMediaId(mediaId, extras)
        Timber.tag(TAG).d( "onPrepareFromMediaId: ${mediaId}")
    }

    override fun onPrepare() {
        super.onPrepare()
        Timber.tag(TAG).d( "onPrepare")
    }

    override fun onPlay() {
        super.onPlay()
        applicationScope.launch {
            Timber.tag(TAG).d( "onPlay")
            musicControllerUsecase.onPlay(
                addToPlayingQueue = false
            )
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.tag(TAG).d( "onStop")

        applicationScope.launch {
            musicControllerUsecase.onStop()
        }
    }

    override fun onPause() {
        super.onPause()
        applicationScope.launch {
            Timber.tag(TAG).d( "onPause")
            musicControllerUsecase.onPause()
        }
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        applicationScope.launch {
            Timber.tag(TAG).d( "onSkipToNext")
            musicControllerUsecase.onNext()
        }
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        applicationScope.launch {
            Timber.tag(TAG).d( "onSkipToPrevious")
            musicControllerUsecase.onPrevious()
        }
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        applicationScope.launch {
            Timber.tag(TAG).d( "onSeekTo - $pos")
            musicControllerUsecase.snapTo(
                duration = pos
            )
        }
    }

    companion object {
        const val PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
        const val ACTION_QUIT = "$PACKAGE_NAME.quitservice"
        const val ACTION_REFRESH = "$PACKAGE_NAME.refresh"
        const val CYCLE_REPEAT = "$PACKAGE_NAME.cyclerepeat"
        const val TOGGLE_SHUFFLE = "$PACKAGE_NAME.toggleshuffle"
        const val TOGGLE_FAVORITE = "$PACKAGE_NAME.togglefavorite"

        const val ACTION_PLAY_PAUSE = "$PACKAGE_NAME.play_pause"
        const val ACTION_NEXT = "$PACKAGE_NAME.next"
        const val ACTION_PREVIOUS = "$PACKAGE_NAME.previous"

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