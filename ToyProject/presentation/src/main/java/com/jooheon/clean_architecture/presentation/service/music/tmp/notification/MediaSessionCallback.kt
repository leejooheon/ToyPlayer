package com.jooheon.clean_architecture.presentation.service.music.tmp.notification

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import kotlinx.coroutines.launch

class MediaSessionCallback(
    private val musicService: MusicService
) : MediaSessionCompat.Callback() {
    private val TAG = MusicService::class.java.simpleName + "@" + MediaSessionCallback::class.java.simpleName
    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
        Log.d(TAG, "onCustomAction - $action")
    }
    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
        Log.d(TAG, "onPlayFromMediaId - $mediaId")
    }

    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        super.onPlayFromSearch(query, extras)
        Log.d(TAG, "onPlayFromSearch - $query")
    }

    override fun onPrepare() {
        super.onPrepare()
        Log.d(TAG, "onPrepare")
    }

    override fun onPlay() {
        super.onPlay()
        Log.d(TAG, "onPlay")
        musicService.run {
            serviceScope.launch {
                val currentMusic = musicService.musicController.currentPlayingMusic.value
                musicService.musicController.play(
                    currentMusic
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        musicService.run {
            serviceScope.launch {
                musicService.musicController.stop()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        musicService.run {
            serviceScope.launch {
                musicService.musicController.pause()
            }
        }
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
        Log.d(TAG, "onSkipToNext")

        musicService.run {
            serviceScope.launch {
                musicService.musicController.next()
            }
        }
    }

    override fun onSkipToPrevious() {
        super.onSkipToPrevious()
        Log.d(TAG, "onSkipToPrevious")

        musicService.run {
            serviceScope.launch {
                musicService.musicController.previous()
            }
        }
    }

    override fun onSeekTo(pos: Long) {
        super.onSeekTo(pos)
        Log.d(TAG, "onSeekTo - $pos")
        musicService.run {
            serviceScope.launch {
                musicService.musicController.snapTo(pos)
            }
        }
    }
}