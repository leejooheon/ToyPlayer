package com.jooheon.toyplayer.features.musicservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
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
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess

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

    private lateinit var mediaSession: MediaLibrarySession
    private lateinit var mediaLibrarySessionCallback: MediaLibrarySessionCallback
    private lateinit var customMediaNotificationProvider: CustomMediaNotificationProvider

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var notificationManager: NotificationManager? = null

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    override fun onCreate() {
        Timber.tag(LifecycleTAG).d( "onCreate")
        super.onCreate()

        initMediaSession()
        initNotification()
        initListener()
        initUseCase()

        collectStates()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.tag(LifecycleTAG).d( "onTaskRemoved - 1")
        if (!mediaSession.player.playWhenReady) {
            Timber.tag(LifecycleTAG).d( "onTaskRemoved - 2")

            // If the player isn't set to play when ready, the service is stopped and resources released.
            // This is done because if the app is swiped away from recent apps without this check,
            // the notification would remain in an unresponsive state.
            // Further explanation can be found at: https://github.com/androidx/media/issues/167#issuecomment-1615184728
            stopSelf()
        }
    }

    override fun onDestroy() {
        Timber.tag(LifecycleTAG).d( "onDestroy")
        release()
        super.onDestroy()
    }

    private fun release() {
        playbackUriResolver.release()
        playbackCacheManager.release()
        mediaLibrarySessionCallback.release()

        mediaSession.release()
        mediaSession.player.release()

        clearListener()
        serviceScope.cancel()

//        handleMedia3Bug()
    }

    private fun handleMedia3Bug() { // TODO: CleanUp
        /**
         * process가 종료되지 않는 버그.. 짱난다 demo-session도 동일
         * we should have something else instead\
         * https://github.com/androidx/media/issues/805
         **/
        notificationManager?.cancelAll()
        exitProcess(0)
    }

    private fun initPlayer(): Player {
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
            .build()

        return ToyPlayer(exoPlayer)
    }

    private fun initNotification() {
        customMediaNotificationProvider = CustomMediaNotificationProvider(
            context = this,
            notificationIdProvider = { NOTIFICATION_ID },
            channelId = NOTIFICATION_CHANNEL_ID,
            channelNameResourceId = R.string.playing_notification_name,
        )
        setMediaNotificationProvider(customMediaNotificationProvider)
    }

    private fun initMediaSession() {
        val player = initPlayer()

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
        mediaSession.player.addListener(playbackListener)
    }

    private fun initUseCase() = serviceScope.launch {
        playbackUseCase.initialize(mediaSession.player, serviceScope)
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
                musicStateHolder.isPlaying.collectLatest { isPlaying ->
                    if(isPlaying) {
                        withContext(Dispatchers.Main) {
                            pollCurrentDuration(mediaSession.player).collect {
                                    value -> musicStateHolder.onCurrentDurationChanged(value)
                            }
                        }
                    }
                }
            }

            launch {
                playbackErrorUseCase.autoPlayChannel.collectLatest {
                    val player = mediaSession.player
                    if(!player.isPlaying) player.play()
                }
            }
        }
    }

    private fun pollCurrentDuration(player: Player) = flow {
        while (player.isPlaying && (player.currentPosition + POLL_INTERVAL_MSEC <= player.duration)) {
            emit(player.currentPosition)
            delay(POLL_INTERVAL_MSEC)
        }
    }.conflate()

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            singleTopActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        Timber.tag(LifecycleTAG).d("onBind: ${intent?.component}")
        return super.onBind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Timber.tag(LifecycleTAG).d("onUnbind: ${intent?.component}")
        return super.onUnbind(intent)
    }
    override fun onRebind(intent: Intent?) {
        Timber.tag(LifecycleTAG).d("onRebind: ${intent?.component}")
        super.onRebind(intent)
    }
    companion object {
        private const val PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
        private const val POLL_INTERVAL_MSEC = 500L

        private const val LifecycleTAG = "ServiceLifecycle"

        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"

        const val CYCLE_REPEAT = "$PACKAGE_NAME.cycle_repeat"
        const val TOGGLE_SHUFFLE = "$PACKAGE_NAME.toggle_shuffle"
    }
}