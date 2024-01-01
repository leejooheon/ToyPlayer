package com.jooheon.clean_architecture.features.musicservice

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSourceBitmapLoader
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.session.CacheBitmapLoader
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.jooheon.clean_architecture.features.musicservice.data.MediaItemProvider
import com.jooheon.clean_architecture.features.musicservice.notification.CustomMediaNotificationProvider
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicPlayerListener
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.toyproject.features.musicservice.BuildConfig
import com.jooheon.clean_architecture.toyproject.features.musicservice.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MusicService: MediaLibraryService() {
    private val TAG = MusicService::class.java.simpleName + "@" + "Main"

    @Inject
    lateinit var musicControllerUseCase: MusicControllerUseCase

    @Inject
    lateinit var musicPlayerListener: MusicPlayerListener

    @Inject
    lateinit var singleTopActivityIntent: Intent

    @Inject
    lateinit var playbackUriResolver: PlaybackUriResolver

    @Inject
    lateinit var playbackCacheManager: PlaybackCacheManager

    @Inject
    lateinit var mediaItemProvider: MediaItemProvider

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var customMediaSessionCallback: CustomMediaSessionCallback

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var notificationManager: NotificationManager? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaSession
    }

    override fun onCreate() {
        Timber.tag(TAG).d( "onCreate")
        super.onCreate()
        initPlayer()
        initNotification()
        initMediaSession()
        initListener()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.tag(TAG).d( "onLowMemory")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Timber.tag(TAG).d( "onTaskRemoved - 1")
        if (!exoPlayer.playWhenReady || exoPlayer.mediaItemCount == 0) {
            Timber.tag(TAG).d( "onTaskRemoved - 2")
            release()
            stopSelf()
        }
    }


    private fun release() {
        notificationManager?.cancel(NOTIFICATION_ID)
        playbackUriResolver.release()
        playbackCacheManager.release()

        // clear listener
        customMediaSessionCallback.release()
        musicPlayerListener.release()
        clearListener()

        mediaSession.setSessionActivity(getBackStackedActivity())
        mediaSession.release()
        exoPlayer.release()

        serviceScope.cancel()
    }

    override fun onDestroy() {
        Timber.tag(TAG).d( "onDestroy")
        release()
        super.onDestroy()
    }

    private fun initPlayer() {
        playbackCacheManager.init()
        playbackUriResolver.init(playbackCacheManager)

        val mediaSourceFactory = DefaultMediaSourceFactory(
            ResolvingDataSource.Factory(
                playbackCacheManager.cacheDataSource(),
                playbackUriResolver
            ),
            DefaultExtractorsFactory()
        )

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .setWakeMode(C.WAKE_MODE_NETWORK) // 잠금화면에서 Wifi를 이용한 백그라운드 재생 허용
            .build()
    }

    private fun initNotification() {
        notificationManager = getSystemService()
        CustomMediaNotificationProvider(
            context = this,
            notificationIdProvider = { NOTIFICATION_ID },
            channelId = NOTIFICATION_CHANNEL_ID,
            channelNameResourceId = R.string.playing_notification_name,
        ).also {
            setMediaNotificationProvider(it)
        }

        customMediaSessionCallback = CustomMediaSessionCallback(
            context = this,
            mediaItemProvider = mediaItemProvider,
        )
    }

    private fun initMediaSession() {
        mediaSession = MediaLibrarySession.Builder(
            /** service  **/this,
            /** player   **/ customForwardingPlayer,
            /** callback **/ customMediaSessionCallback,
        )
        .setSessionActivity(getSingleTopActivity())
//        .setBitmapLoader(CoilBitmapLoader(this, serviceScope))
        .setBitmapLoader(CacheBitmapLoader(DataSourceBitmapLoader(/* context= */ this)))
        .build()

    }
    private fun initListener() {
        musicPlayerListener.setPlayer(exoPlayer)
        setListener(MediaSessionServiceListener())
        customMediaSessionCallback.initEventListener { event ->
            when(event) {
                is CustomMediaSessionCallback.CustomEvent.OnRepeatIconPressed -> musicControllerUseCase.onRepeatButtonPressed()
                is CustomMediaSessionCallback.CustomEvent.OnShuffleIconPressed -> musicControllerUseCase.onShuffleButtonPressed()
            }
        }
    }

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            singleTopActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val customForwardingPlayer by lazy {
        object : ForwardingPlayer(exoPlayer) {
            override fun getAvailableCommands(): Player.Commands {
                return super.getAvailableCommands().buildUpon()
                    .add(Player.COMMAND_SEEK_TO_NEXT)
                    .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                    .build()
            }

            override fun isCommandAvailable(command: Int): Boolean {
                // https://github.com/androidx/media/issues/140
                val available = when(command) {
                    COMMAND_SEEK_TO_NEXT -> true
                    COMMAND_SEEK_TO_PREVIOUS -> true
                    else -> super.isCommandAvailable(command)
                }

                return available
            }

            override fun play() {
                if(fromSystemUi()) musicControllerUseCase.onPlay()
                else super.play()
            }
            override fun pause() {
                if(fromSystemUi()) musicControllerUseCase.onPause()
                else super.pause()
            }
            override fun seekToNext() {
                musicControllerUseCase.onNext()
            }
            override fun seekToPrevious() {
                musicControllerUseCase.onPrevious()
            }
            override fun stop() {
                musicControllerUseCase.onStop()
            }

            private fun fromSystemUi(): Boolean {
                try {
                    val controllerInfo = mediaSession.controllerForCurrentRequest ?: return false
                    return mediaSession.isMediaNotificationController(controllerInfo)
                } catch (e: NullPointerException) {
                    return false
                }
            }
        }
    }

    private fun getBackStackedActivity(): PendingIntent {
        return TaskStackBuilder.create(this).run {
            addNextIntent(singleTopActivityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
    }
    private inner class MediaSessionServiceListener : Listener {
        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        @SuppressLint("MissingPermission") // TODO: b/280766358 - Request this permission at runtime.
        override fun onForegroundServiceStartNotAllowedException() {
            val notificationManagerCompat = NotificationManagerCompat.from(this@MusicService)
            ensureNotificationChannel(notificationManagerCompat)
            val pendingIntent = getBackStackedActivity()
            val builder =
                NotificationCompat.Builder(this@MusicService, NOTIFICATION_CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.media3_notification_small_icon)
                    .setContentTitle(getString(R.string.playing_notification_error_title))
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.playing_notification_error_content))
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
        private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
            if (notificationManagerCompat.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) {
                return
            }

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.playing_notification_error_title),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME

        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"

        const val CYCLE_REPEAT = "$PACKAGE_NAME.cycle_repeat"
        const val TOGGLE_SHUFFLE = "$PACKAGE_NAME.toggle_shuffle"

        fun allowedCaller(caller: String): Boolean {
            val me = "com.jooheon.clean_architecture.toyproject"
            val androidAuto = "com.google.android.projection.gearhead"
            val wearOs = "com.google.android.wearable.app"
            val androidAutoSimulator = "com.google.android.autosimulator"

            val validPackageNames = listOf(me, androidAuto, wearOs, androidAutoSimulator)
            return validPackageNames.contains(caller)
        }
    }
}