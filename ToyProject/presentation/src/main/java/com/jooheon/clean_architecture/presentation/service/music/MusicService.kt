package com.jooheon.clean_architecture.presentation.service.music

import android.app.PendingIntent
import android.content.Intent
import android.media.session.MediaSession
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.view.KeyEvent
import androidx.media.MediaBrowserServiceCompat
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerUseCase
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {
    private val TAG = MusicService::class.java.simpleName

    private val musicBind: IBinder = MediaPlayerServiceBinder()

    @Inject
    lateinit var musicController: MusicController

    private lateinit var mediaSession: MediaSession

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        Log.d(TAG, "musicController - ${musicController}")
        initialize()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    private fun initialize() {
        Log.d(TAG, "initialize")
        musicController.loadMusic(serviceScope)
        initMediaSession()
    }

    private fun initMediaSession() {
        mediaSession = MediaSession(this, MEDIA_SESSION).apply {
            setCallback(object : MediaSession.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val event = if(Intent.ACTION_MEDIA_BUTTON == mediaButtonIntent.action) {
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT) as? KeyEvent
                    } else { null }

//                    event?.let {
//                        when (it.keyCode) {
//                            KeyEvent.KEYCODE_MEDIA_PLAY -> Log.d(TAG, "KEYCODE_MEDIA_PLAY")
//                            KeyEvent.KEYCODE_MEDIA_PAUSE -> Log.d(TAG, "KEYCODE_MEDIA_PLAY")
//                            KeyEvent.KEYCODE_MEDIA_NEXT -> Log.d(TAG, "KEYCODE_MEDIA_PLAY")
//                            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> Log.d(TAG, "KEYCODE_MEDIA_PLAY")
//                            else -> Log.d(TAG, "KEYCODE_ELSE")
//                        }
//                    }
                    return true
                }
            })
        }

    }

    override fun onBind(intent: Intent): IBinder {
        // For Android auto, need to call super, or onGetRoot won't be called.
        return if ("android.media.browse.MediaBrowserService" == intent.action) {
            super.onBind(intent)!!
        } else musicBind
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?,
    ): BrowserRoot {
        Log.d(TAG, "onGetRoot")
        val browserRootPath = MEDIA_ID_ROOT
        return BrowserRoot(browserRootPath, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>,
    ) {
        Log.d(TAG, "onLoadChildren")
        result.detach()

//        if(parentId != MEDIA_ID_ROOT) {
//            Log.e(TAG, "onLoadChildren: requested parentId is invalid -> ${parentId}")
//            return
//        }
//        val resultSend = musicDataSource.loadMusic(serviceScope).whenReady { isReady ->
//            Log.e(TAG, "onLoadChildren: isReady: ${isReady}")
//            if(!isReady) {
//                result.sendResult(emptyList())
//                return@whenReady
//            }
//            val list = musicDataSource.allMusicAsMediaItem.toMutableList()
//            result.sendResult(list)
//        }
//        if(!resultSend) result.detach()
    }

    private fun runOnUiThread(content: () -> Unit) {
        serviceScope.launch(Dispatchers.Main) {
            content.invoke()
        }
    }

    companion object {
        const val MEDIA_ID_ROOT = "__ROOT__"
        private const val MEDIA_SESSION = "JooheonMediaSession"


        const val DURATION = "duration"
        const val TRACK_NUMBER = "track_number"
        const val YEAR = "year"
        const val DATA = "data"
        const val DATE_MODIFIED = "date_modified"
        const val ALBUM_ID = "album_id"
        const val ALBUM_NAME = "album_name"
        const val ALBUM_ARTIST = "album_artist"
        const val ARTIST_ID = "artist_id"
        const val ARTIST_NAME = "artist_name"
        const val COMPOSER = "composer"
    }

    inner class MediaPlayerServiceBinder : Binder() {
        fun getService() = this@MusicService
    }
}