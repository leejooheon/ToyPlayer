package com.jooheon.toyplayer.features.musicservice

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.jooheon.toyplayer.features.musicservice.di.MusicServiceCoroutineScope
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationCommand
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationProvider
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager
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
import kotlin.system.exitProcess

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class MusicService: MediaLibraryService() {
    @Inject
    @MusicServiceCoroutineScope
    lateinit var serviceScope: CoroutineScope

    @Inject
    lateinit var musicStateHolder: MusicStateHolder

    @Inject
    lateinit var playbackListener: PlaybackListener

    @Inject
    lateinit var playbackCacheManager: PlaybackCacheManager

    @Inject
    lateinit var singleTopActivityIntent: Intent

    @Inject
    lateinit var toyPlayer: ToyPlayer

    @Inject
    lateinit var mediaLibrarySessionCallback: MediaLibrarySessionCallback

    @Inject
    lateinit var bitmapLoader: BitmapLoader

    @Inject
    lateinit var playbackLogUseCase : PlaybackLogUseCase

    @Inject
    lateinit var playbackUseCase : PlaybackUseCase

    @Inject
    lateinit var playbackErrorUseCase : PlaybackErrorUseCase

    private var mediaSession: MediaLibrarySession? = null
    private lateinit var customMediaNotificationProvider: CustomMediaNotificationProvider

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
        val player = mediaSession?.player ?: run {
            stopSelf()
            return
        }
        Timber.tag(LifecycleTAG).d( "onTaskRemoved - playWhenReady: ${player.playWhenReady} mediaItemCount: ${player.mediaItemCount}")
        if (!player.playWhenReady || player.mediaItemCount == 0 || player.playbackState == Player.STATE_ENDED) {
            // If the player isn't set to play when ready, the service is stopped and resources released.
            // This is done because if the app is swiped away from recent apps without this check,
            // the notification would remain in an unresponsive state.
            // Further explanation can be found at: https://github.com/androidx/media/issues/167#issuecomment-1615184728
        }
        stopSelf()
    }

    override fun onDestroy() {
        Timber.tag(LifecycleTAG).d( "onDestroy")
        release()
        super.onDestroy()
    }

    private fun release() {
        playbackCacheManager.release()
        mediaLibrarySessionCallback.release()

        mediaSession?.let {
            it.player.release()
            release()
        }

        clearListener()
        serviceScope.cancel()
//        handleMedia3Bug()
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
        mediaSession = MediaLibrarySession.Builder(
            this,
            toyPlayer,
            mediaLibrarySessionCallback,
        ).apply {
            setSessionActivity(getSingleTopActivity())
            setBitmapLoader(bitmapLoader)
//            setBitmapLoader(CoilBitmapLoader(this, serviceScope))
//            setBitmapLoader(CacheBitmapLoader(DataSourceBitmapLoader(/* context= */ this)))
        }.build()
    }

    private fun initListener() {
        val player = mediaSession?.player ?: run {
            stopSelf()
            return
        }

        musicStateHolder.observeStates(serviceScope)
        playbackListener.observeDuration(serviceScope, player)
        player.addListener(playbackListener)
    }

    private fun initUseCase() {
        playbackUseCase.initialize(mediaSession?.player, serviceScope)
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
                    mediaSession?.setCustomLayout(
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
                    val player = mediaSession?.player ?: return@collectLatest
                    if(!player.isPlaying) player.play()
                }
            }
        }
    }

    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            singleTopActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
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
        private const val PACKAGE_NAME = "toyplayer.musicservice"

        private const val LifecycleTAG = "ServiceLifecycle"

        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"

        const val CYCLE_REPEAT = "$PACKAGE_NAME.cycle_repeat"
        const val TOGGLE_SHUFFLE = "$PACKAGE_NAME.toggle_shuffle"
    }
}