package com.jooheon.clean_architecture.features.musicservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.session.PlaybackState
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media.app.NotificationCompat
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.features.common.utils.GlideUtil
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.ACTION_NEXT
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.ACTION_PLAY_PAUSE
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.ACTION_PREVIOUS
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.ACTION_QUIT
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.ACTION_REFRESH
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.CYCLE_REPEAT
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.MEDIA_SESSION_ACTIONS
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback.Companion.TOGGLE_SHUFFLE
import com.jooheon.clean_architecture.features.musicservice.data.*
import com.jooheon.clean_architecture.features.musicservice.notification.PlayingNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaBrowserServiceCompat() {
    private val TAG = MusicService::class.java.simpleName

    @Inject
    lateinit var mediaSessionCallback: MediaSessionCallback

    @Inject
    lateinit var rootActivityIntent: Intent

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaStyle: NotificationCompat.MediaStyle
    private lateinit var notificationManager: NotificationManager
    private lateinit var playingNotificationManager: PlayingNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val musicBind: IBinder = MediaPlayerServiceBinder()
    private var isForegroundService = false

    private var notifyJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null) {
            Timber.tag(TAG).d( "onStartCommand: intent is null")
            return START_NOT_STICKY
        }

        intent.action?.let {
            Timber.tag(TAG).d( "onStartCommand: action: ${it}")
            doAction(it)
            return START_NOT_STICKY
        }

        val musicState = intent.getParcelableExtra<MusicState>(MUSIC_STATE) ?: return START_NOT_STICKY
        val duration = intent.getLongExtra(MUSIC_DURATION, 0L)

        if(musicState.currentPlayingMusic == Song.default) {
            Log.w(TAG, "received music_state is empty")
            return START_NOT_STICKY
        }

        update(musicState, duration)

        return START_NOT_STICKY
    }

    private fun doAction(action: String) {
        when(action) {
            ACTION_QUIT -> quit()
            ACTION_PLAY_PAUSE -> mediaSessionCallback.onPlay()
            ACTION_NEXT -> mediaSessionCallback.onSkipToNext()
            ACTION_PREVIOUS -> mediaSessionCallback.onSkipToPrevious()
        }
    }
    private fun update(
        musicState: MusicState,
        duration: Long
    ) {
        Timber.tag(TAG).d( "update mediaSession & notification")
        updateMediaSession(musicState, duration)

        notifyJob?.cancel()
        notifyJob = serviceScope.launch(Dispatchers.IO) {
            notify(
                musicState = musicState,
                albumArtBitmap = null
            )

            val albumArtBitmap = async {
                val albumArtUri = musicState.currentPlayingMusic.albumArtUri
                GlideUtil.asBitmap(applicationContext, albumArtUri)
            }.await()

            notify(
                musicState = musicState,
                albumArtBitmap = albumArtBitmap
            )
        }
    }

    private fun notify(
        musicState: MusicState,
        albumArtBitmap: Bitmap?
    ) {
        notificationManager.notify(
            PlayingNotificationManager.NOTIFICATION_ID,
            playingNotificationManager.notificationMediaPlayer(
                context = applicationContext,
                mediaStyle = mediaStyle,
                state = musicState,
                bitmap = albumArtBitmap,
            ).also {
                isForegroundService = true
            }
        )
    }

    override fun onCreate() {
        super.onCreate()

        Timber.tag(TAG).d( "onCreate")
        initialize()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.tag(TAG).d( "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        quit()
    }

    override fun onLowMemory() {
        Timber.tag(TAG).d( "onLowMemory")
        super.onLowMemory()
        serviceScope.launch {
            mediaSessionCallback.onStop()
        }
    }
    private fun initialize() {
        Timber.tag(TAG).d( "initialize")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        playingNotificationManager = PlayingNotificationManager(
            context = this,
            rootActivityIntent = rootActivityIntent,
            notificationManager = notificationManager,
        )

        setupMediaSession()
    }

    private fun setupMediaSession() {
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        mediaSession = MediaSessionCompat(
            this,
            "com.nhn.android.lite.bugs",
        ).apply {
            isActive = true
            setCallback(mediaSessionCallback)
            setSessionActivity(activityIntent)
        }.also {
            val sessionToken = it.sessionToken

            this@MusicService.sessionToken = sessionToken
            mediaStyle = NotificationCompat.MediaStyle().setMediaSession(sessionToken)
//            mediaStyle = NotificationCompat.MediaStyle().setShowActionsInCompactView(1, 3)
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
        Timber.tag(TAG).d( "onGetRoot")
        val browserRootPath = MEDIA_ID_ROOT
        return BrowserRoot(browserRootPath, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>,
    ) {
        Timber.tag(TAG).d( "onLoadChildren")
        result.detach()
    }

    private fun quit() {
        Timber.tag(TAG).d( "quit")
        mediaSessionCallback.onStop()

        mediaSession.isActive = false
        mediaSession.release()

//        notificationManager.cancelAll()

        stopForeground(STOP_FOREGROUND_REMOVE).also {
            isForegroundService = false
        }

        stopSelf()
    }

    private fun updateMediaSession(state: MusicState, duration: Long) {
        val playbackStateBuilder = PlaybackStateCompat.Builder()
            .setActions(MEDIA_SESSION_ACTIONS)
            .setState(
                playbackState(state),
                duration,
                1f
            ).also {
                setCustomAction(state, it)
            }

        mediaSession.apply {
            setPlaybackState(playbackStateBuilder.build())
            setMetadata(toMediaMetadataCompat(state.currentPlayingMusic))
        }
    }

    private fun setCustomAction(state: MusicState, builder: PlaybackStateCompat.Builder) {
        val repeatIconResId = when(state.repeatMode) {
            RepeatMode.REPEAT_ALL -> R.drawable.ic_repeat_white_circle
            RepeatMode.REPEAT_ONE -> R.drawable.ic_repeat_one
            RepeatMode.REPEAT_OFF -> R.drawable.ic_repeat
        }

        val shuffleIconResId = when(state.shuffleMode) {
            ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_on_circled
            ShuffleMode.NONE -> R.drawable.ic_shuffle_off_circled
        }

        val refreshIconResId = R.drawable.ic_refresh_24

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

    private fun playbackState(state: MusicState) = if(state.isPlaying) {
        PlaybackState.STATE_PLAYING
    } else {
        PlaybackState.STATE_PAUSED
    }

    private fun toMediaMetadataCompat(song: Song) = MediaMetadataCompat.Builder().apply {
        title = song.title
        album = song.album
        albumArtUri = song.albumArtUri.toString()
        artist = song.artist
        duration = song.duration
    }.build()

    private fun parseMediaMetadataCompat(state: MusicState): MediaMetadataCompat = MediaMetadataCompat.Builder().apply {
        title = state.currentPlayingMusic.title
        album = state.currentPlayingMusic.album
        albumArtUri = state.currentPlayingMusic.albumArtUri.toString()
        artist = state.currentPlayingMusic.artist
        duration = state.currentPlayingMusic.duration
    }.build()

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
}