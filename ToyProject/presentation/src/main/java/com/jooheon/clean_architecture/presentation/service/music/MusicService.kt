package com.jooheon.clean_architecture.presentation.service.music

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.BuildConfig
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicState
import com.jooheon.clean_architecture.presentation.service.music.tmp.MusicController
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaButtonIntentReceiver
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaSessionCallback
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaSessionCallback.Companion.CYCLE_REPEAT
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaSessionCallback.Companion.MEDIA_SESSION_ACTIONS
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaSessionCallback.Companion.TOGGLE_SHUFFLE
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.PlayingNotificationManager
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.PlayingNotificationManager.Companion.NOTIFICATION_ID
import com.jooheon.clean_architecture.presentation.utils.MusicUtil
import com.jooheon.clean_architecture.presentation.utils.VersionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {
    private val TAG = MusicService::class.java.simpleName

    @Inject
    lateinit var musicController: MusicController

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaStyle: MediaStyle
    private lateinit var notificationManager: NotificationManager
    private lateinit var playingNotificationManager: PlayingNotificationManager

    private val serviceJob = Job()
    val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val musicBind: IBinder = MediaPlayerServiceBinder()
    private var isForegroundService = false

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
        if(intent == null || !isForegroundService) { return START_NOT_STICKY }

        val musicState = intent.getParcelableExtra<MusicState>(MUSIC_STATE) ?: return START_NOT_STICKY
        val duration = intent.getLongExtra(MUSIC_DURATION, 0L)

        updateMediaSession(musicState, duration)
        notificationManager.notify(
            NOTIFICATION_ID,
            playingNotificationManager.notificationMediaPlayer(
                applicationContext,
                mediaStyle,
                musicState
            )
        )

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

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, getIntentFlags())
        }

        mediaSession = MediaSessionCompat(
            this,
            BuildConfig.APPLICATION_ID,
            mediaButtonReceiverComponentName,
            mediaButtonReceiverPendingIntent
        ).apply {
            isActive = true
            setSessionActivity(activityIntent)
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
    }

    private fun runOnUiThread(content: () -> Unit) {
        serviceScope.launch(Dispatchers.Main) {
            content.invoke()
        }
    }

    private fun updateMediaSession(state: MusicState, duration: Long) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(MEDIA_SESSION_ACTIONS)
            .setState(
                getPlayingState(state),
                duration,
                1f
            ).also {
                setCustomAction(state, it)
            }

        mediaSession.apply {
            setPlaybackState(playbackStateBuilder.build())
            setMetadata(MusicUtil.parseMediaMetadataCompatFromMusicState(state))
        }
    }

    private fun setCustomAction(state: MusicState, builder: PlaybackStateCompat.Builder) {
        val repeatIconResId = when(state.repeatMode) {
            Entity.RepeatMode.REPEAT_ALL -> R.drawable.ic_repeat_white_circle
            Entity.RepeatMode.REPEAT_ONE -> R.drawable.ic_repeat_one
            Entity.RepeatMode.REPEAT_OFF -> R.drawable.ic_repeat
        }

        val shuffleIconResId = when(state.shuffleMode) {
            Entity.ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_on_circled
            Entity.ShuffleMode.NONE -> R.drawable.ic_shuffle_off_circled
        }

        builder.apply {
            addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    CYCLE_REPEAT, getString(R.string.action_cycle_repeat), repeatIconResId
                ).build()
            )
            addCustomAction(
                PlaybackStateCompat.CustomAction.Builder(
                    TOGGLE_SHUFFLE, getString(R.string.action_toggle_shuffle), shuffleIconResId
                ).build()
            )
        }
    }

    private fun getPlayingState(state: MusicState) = if(state.isPlaying) {
        PlaybackState.STATE_PLAYING
    } else {
        PlaybackState.STATE_PAUSED
    }

    companion object {
        const val MEDIA_ID_ROOT = "__ROOT__"
        const val MUSIC_STATE = "MusicState"
        const val MUSIC_DURATION = "MusicDuration"

        fun startService(context: Context, intent: Intent) {
            try {
                // IMPORTANT NOTE: (kind of a hack)
                // on Android O and above the following crashes when the app is not running
                // there is no good way to check whether the app is running so we catch the exception
                // we do not always want to use startForegroundService() because then one gets an ANR
                // if no notification is displayed via startForeground()
                // according to Play analytics this happens a lot, I suppose for example if command = PAUSE
                context.startService(intent)
            } catch (ignored: IllegalStateException) {
                runCatching {
                    ContextCompat.startForegroundService(context, intent)
                }
            }
        }
    }

    inner class MediaPlayerServiceBinder : Binder() {
        fun getService() = this@MusicService
    }

    private fun getIntentFlags(): Int {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or if (VersionUtils.hasMarshmallow()) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        return flags
    }
}