package com.jooheon.clean_architecture.presentation.service.music.tmp.notification

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import kotlinx.coroutines.launch

class MediaSessionCallback(
    musicService: MusicService
) : MediaSessionCompat.Callback() {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaSessionCallback::class.java.simpleName

    private val scope = musicService.serviceScope
    private val musicController = musicService.musicController

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        Log.d(TAG, "onCustomAction - $action")

        scope.launch {
            when (action) {
                CYCLE_REPEAT -> musicController.changeRepeatMode()
                TOGGLE_SHUFFLE -> musicController.changeShuffleMode()
            }
        }
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        Log.d(TAG, "onPlayFromMediaId - $mediaId")
        // MediaControllerTest 앱에서 Play 버튼 누를 시 호출된다.

        scope.launch {
            musicController.run {
                songs.value.firstOrNull {
                    it.id == mediaId?.toLongOrNull()
                }?.also {
                    play(it)
                }
            }
        }
    }

    override fun onPlayFromUri(uri: Uri?, extras: Bundle?) {
        super.onPlayFromUri(uri, extras)
        Log.d(TAG, "onPlayFromUri - ${uri}")

        scope.launch {
            musicController.play(uri)
        }
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        Log.d(TAG, "onPlayFromSearch - ${query}")

        if(query == null) return

        scope.launch {
            musicController.run {
                songs.value.firstOrNull {
                    it.title.contains(query, true)
                }?.also {
                    play(it)
                }
            }
        }
    }

    override fun onPrepare() {
        super.onPrepare()
        Log.d(TAG, "onPrepare")
    }

    override fun onPlay() {
        super.onPlay()
        Log.d(TAG, "onPlay")

        scope.launch {
            val currentMusic = musicController.currentPlayingMusic.value
            musicController.play(currentMusic)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")

        scope.launch {
            musicController.stop()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")

        scope.launch {
            musicController.pause()
        }
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        Log.d(TAG, "onSkipToNext")

        scope.launch {
            musicController.next()
        }
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        Log.d(TAG, "onSkipToPrevious")

        scope.launch {
            musicController.previous()
        }
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        Log.d(TAG, "onSeekTo - $pos")

        scope.launch {
            musicController.snapTo(
                duration = pos,
                fromUser = true
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