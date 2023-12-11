package com.jooheon.clean_architecture.features.musicservice

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.jooheon.clean_architecture.features.musicservice.notification.CoilBitmapLoader
import com.jooheon.clean_architecture.features.musicservice.notification.CustomMediaNotificationProvider
import com.jooheon.clean_architecture.features.musicservice.notification.CustomMediaSessionCallback
import com.jooheon.clean_architecture.features.musicservice.usecase.MediaControllerManager
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.toyproject.features.musicservice.BuildConfig
import com.jooheon.clean_architecture.toyproject.features.musicservice.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.internal.notifyAll
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaLibraryService(), MediaLibraryService.MediaLibrarySession.Callback {
    private val TAG = MusicService::class.java.simpleName + "@" + "Main"

    @Inject
    lateinit var musicControllerUseCase: MusicControllerUseCase

    @Inject
    lateinit var singleTopActivityIntent: Intent

    @Inject
    lateinit var mediaControllerManager: MediaControllerManager

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mediaSession: MediaLibrarySession

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = mediaSession

    @UnstableApi
    override fun onCreate() {
        Timber.tag(TAG).d( "onCreate")
        super.onCreate()
        initPlayer()
        initNotification()
        initMediaSession()
    }

    @UnstableApi
    override fun onDestroy() {
        Timber.tag(TAG).d( "onDestroy")
        musicControllerUseCase.release()
        mediaControllerManager.release()
        exoPlayer.release()
        mediaSession.release()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun initPlayer() {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        exoPlayer = ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .setWakeMode(C.WAKE_MODE_NETWORK) // 잠금화면에서 Wifi를 이용한 백그라운드 재생 허용
            .build()
    }

    @UnstableApi
    private fun initNotification() {
        val mediaNotificationProvider = CustomMediaNotificationProvider(
            context = this,
            notificationIdProvider = { NOTIFICATION_ID },
            channelId = NOTIFICATION_CHANNEL_ID,
            channelNameResourceId = R.string.playing_notification_name,
        )

        setMediaNotificationProvider(mediaNotificationProvider)
    }

    @UnstableApi
    private fun initMediaSession() {

        mediaSession = MediaLibrarySession.Builder(
            /** service **/this,
            /** player **/ customForwardingPlayer,
            /** callback **/ CustomMediaSessionCallback(
                context = this,
                musicControllerUseCase = musicControllerUseCase,
            )
        ).setSessionActivity(
            PendingIntent.getActivity(
                this, 0, singleTopActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        ).setBitmapLoader(
            CoilBitmapLoader(this, serviceScope)
        ).build()

        mediaControllerManager.init()
        musicControllerUseCase.setPlayer(exoPlayer)
    }

    private val customForwardingPlayer by lazy {
        @UnstableApi
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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.tag(TAG).d( "onTaskRemoved")
        quit()
    }

    private fun quit() {
        if (!exoPlayer.isPlaying) {
            Timber.tag(TAG).d( "quit")
            exoPlayer.playWhenReady = false
            exoPlayer.pause()
            stopSelf()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.tag(TAG).d( "onLowMemory")
    }

    companion object {
        private const val PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME

        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"

        const val CYCLE_REPEAT = "$PACKAGE_NAME.cycle_repeat"
        const val TOGGLE_SHUFFLE = "$PACKAGE_NAME.toggle_shuffle"
    }
}