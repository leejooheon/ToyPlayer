package com.jooheon.clean_architecture.presentation.service.music

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.session.PlaybackState
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.jooheon.clean_architecture.presentation.BuildConfig
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicState
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaButtonIntentReceiver
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaSessionCallback
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.PlayingNotificationManager
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.PlayingNotificationManager.Companion.NOTIFICATION_ID
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.utils.VersionUtils
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

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaStyle: MediaStyle
    private lateinit var notificationManager: NotificationManager
    private lateinit var playingNotificationManager: PlayingNotificationManager
    private var isForegroundService = false

    private val serviceJob = Job()
    val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")
        Log.d(TAG, "musicController - ${musicController}")
        initialize()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSession.isActive = false
        mediaSession.release()
        notificationManager.cancelAll()
        stopForeground(STOP_FOREGROUND_REMOVE).also {
            isForegroundService = false
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        serviceScope.launch { musicController.stop() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        intent?.getParcelableExtra<MusicState>("MusicService")?.also { newState ->
            if(!isForegroundService) return@also
            updateMediaSession(newState)

            notificationManager.notify(
                NOTIFICATION_ID,
                playingNotificationManager.notificationMediaPlayer(
                    applicationContext,
                    mediaStyle,
                    newState
                )
            )
        }

        return START_NOT_STICKY
    }

    private fun initialize() {
        Log.d(TAG, "initialize")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        playingNotificationManager = PlayingNotificationManager(
            context = this,
            notificationManager = notificationManager
        )

        musicController.loadMusic(serviceScope)
        setupMediaSession()
    }

    private fun setupMediaSession() {
        val mediaButtonReceiverComponentName = ComponentName(
            applicationContext,
            MediaButtonIntentReceiver::class.java
        )

        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            component = mediaButtonReceiverComponentName
        }

        val mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            mediaButtonIntent,
            if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE else 0
        )

        mediaSession = MediaSessionCompat(
            this,
            BuildConfig.APPLICATION_ID,
            mediaButtonReceiverComponentName,
            mediaButtonReceiverPendingIntent
        ).apply {
            isActive = true
            setMediaButtonReceiver(mediaButtonReceiverPendingIntent)
            setCallback(MediaSessionCallback(this@MusicService))
        }.also {
            sessionToken = it.sessionToken
            mediaStyle = MediaStyle().setMediaSession(it.sessionToken)
        }

        val notification = playingNotificationManager.foregroundNotification()
        startForeground(NOTIFICATION_ID, notification).also {
            isForegroundService = true
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

    private fun updateMediaSession(state: MusicState) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(MEDIA_SESSION_ACTIONS)
            .setState(
                getPlayingState(state),
                state.currentDuration,
                1f
            )

//        setCustomAction(playbackStateBuilder)
        mediaSession.setPlaybackState(playbackStateBuilder.build())
        mediaSession.setMetadata(
            MusicUtil.parseMediaMetadataCompatFromMusicState(state)
        )
    }

    private fun getPlayingState(state: MusicState) = if(state.isPlaying) {
        PlaybackState.STATE_PLAYING
    } else {
        PlaybackState.STATE_PAUSED
    }

    companion object {
        const val MEDIA_ID_ROOT = "__ROOT__"

        private const val MEDIA_SESSION_ACTIONS = (PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_SEEK_TO)
    }

    inner class MediaPlayerServiceBinder : Binder() {
        fun getService() = this@MusicService
    }
}