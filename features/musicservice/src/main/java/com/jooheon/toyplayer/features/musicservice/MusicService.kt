package com.jooheon.toyplayer.features.musicservice

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
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSourceBitmapLoader
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.session.CacheBitmapLoader
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationCommand
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationProvider
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackListener
import com.jooheon.toyplayer.features.musicservice.player.ToyPlayer
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackErrorUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackLogUseCase
import com.jooheon.toyplayer.features.musicservice.usecase.PlaybackUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MusicService: MediaLibraryService() {
    private val TAG = MusicService::class.java.simpleName + "@" + "Main"

    @Inject
    lateinit var playbackListener: PlaybackListener

    @Inject
    lateinit var singleTopActivityIntent: Intent

    @Inject
    lateinit var playbackUriResolver: PlaybackUriResolver

    @Inject
    lateinit var playbackCacheManager: PlaybackCacheManager

    @Inject
    lateinit var mediaItemProvider: MediaItemProvider

    @Inject
    lateinit var musicStateHolder: MusicStateHolder

    @Inject
    lateinit var playbackLogUseCase : PlaybackLogUseCase

    @Inject
    lateinit var playbackUseCase : PlaybackUseCase

    @Inject
    lateinit var playbackErrorUseCase : PlaybackErrorUseCase

    private lateinit var player: ToyPlayer
    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var mediaLibrarySessionCallback: MediaLibrarySessionCallback
    private lateinit var customMediaNotificationProvider: CustomMediaNotificationProvider

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var notificationManager: NotificationManager? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession.takeUnless { session ->
            session.invokeIsReleased
        }.also {
            if (it == null) {
                Timber.tag(TAG).e("onGetSession returns null because the session is already released")
            }
        }
    }

    override fun onCreate() {
        Timber.tag(TAG).d( "onCreate")
        super.onCreate()
        initPlayer()
        initNotification()
        initMediaSession()
        initListener()
        initUseCase()

        collectStates()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.tag(TAG).d( "onLowMemory")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.tag(TAG).d( "onTaskRemoved - 1")
        if (!player.playWhenReady) {
            Timber.tag(TAG).d( "onTaskRemoved - 2")

            // If the player isn't set to play when ready, the service is stopped and resources released.
            // This is done because if the app is swiped away from recent apps without this check,
            // the notification would remain in an unresponsive state.
            // Further explanation can be found at: https://github.com/androidx/media/issues/167#issuecomment-1615184728
            release()
            stopSelf()
        }
    }

    private fun release() {
        notificationManager?.cancel(NOTIFICATION_ID)

        playbackUriResolver.release()
        playbackCacheManager.release()

        // clear listener
        mediaLibrarySessionCallback.release()
        playbackListener.release()
        clearListener()

        mediaSession.setSessionActivity(getBackStackedActivity())
        mediaSession.release()
        player.release()

        serviceScope.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
        Timber.tag(TAG).d( "onDestroy")
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

        val exoPlayer = ExoPlayer.Builder(applicationContext)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .setWakeMode(C.WAKE_MODE_NETWORK) // 잠금화면에서 Wifi를 이용한 백그라운드 재생 허용
            .build()

        player = ToyPlayer(exoPlayer)
    }

    private fun initNotification() {
        notificationManager = getSystemService()

        customMediaNotificationProvider = CustomMediaNotificationProvider(
            context = this,
            notificationIdProvider = { NOTIFICATION_ID },
            channelId = NOTIFICATION_CHANNEL_ID,
            channelNameResourceId = R.string.playing_notification_name,
        )
        setMediaNotificationProvider(customMediaNotificationProvider)
    }

    private fun initMediaSession() {
        mediaLibrarySessionCallback = MediaLibrarySessionCallback(
            context = this,
            mediaItemProvider = mediaItemProvider,
        )

        mediaSession = MediaLibrarySession.Builder(
            /** service  **/this,
            /** player   **/ player,
            /** callback **/ mediaLibrarySessionCallback,
        )
        .setSessionActivity(getSingleTopActivity())
//        .setBitmapLoader(CoilBitmapLoader(this, serviceScope))
        .setBitmapLoader(CacheBitmapLoader(DataSourceBitmapLoader(/* context= */ this)))
        .build()
    }

    private fun initListener() {
        playbackListener.setPlayer(player)
        setListener(MediaSessionServiceListener())
    }

    private fun initUseCase() = serviceScope.launch {
        playbackUseCase.initialize(player, serviceScope)
        playbackLogUseCase.initialize(serviceScope)
        playbackErrorUseCase.initialize(serviceScope)
    }

    private fun collectStates() {
        serviceScope.launch {
            launch {
                combine(
                    musicStateHolder.repeatMode,
                    musicStateHolder.shuffleMode
                ) { repeatMode, shuffleMode ->
                    repeatMode to shuffleMode
                }.collectLatest { (repeatMode, shuffleMode) ->
                    mediaSession.setCustomLayout(
                        CustomMediaNotificationCommand.layout(
                            context = this@MusicService,
                            shuffleMode = shuffleMode,
                            repeatMode = repeatMode
                        )
                    )
                }
            }

            launch {
                playbackErrorUseCase.autoPlayChannel.collectLatest {
                    if(!player.isPlaying) player.play()
                }
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

    private fun getBackStackedActivity(): PendingIntent {
        return TaskStackBuilder.create(this).run {
            addNextIntent(singleTopActivityIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private val MediaSession.invokeIsReleased: Boolean
        get() = try {
            // temporarily checked to debug
            // https://github.com/androidx/media/issues/422
            MediaSession::class.java.getDeclaredMethod("isReleased")
                .apply { isAccessible = true }
                .invoke(this) as Boolean
        } catch (e: Exception) {
            Timber.tag(TAG).e("Couldn't check if it's released")
            false
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
    }
}