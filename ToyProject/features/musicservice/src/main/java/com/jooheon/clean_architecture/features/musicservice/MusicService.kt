package com.jooheon.clean_architecture.features.musicservice

import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.jooheon.clean_architecture.features.musicservice.notification.PlayingMediaNotificationManager
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import com.jooheon.clean_architecture.toyproject.features.musicservice.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaSessionService() {
    private val TAG = MusicService::class.java.simpleName

    @Inject
    lateinit var musicControllerUsecase: MusicControllerUsecase

    @Inject
    lateinit var mediaSession: MediaSession

    @Inject
    lateinit var playingMediaNotificationManager: PlayingMediaNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    @UnstableApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(intent == null) {
            Timber.tag(TAG).d( "onStartCommand: intent is null")
            return START_NOT_STICKY
        }

        intent.action?.let {
            Timber.tag(TAG).d( "onStartCommand: action: ${it}")
            doAction(it)
            return START_NOT_STICKY
        }

        return START_NOT_STICKY
    }


    @UnstableApi
    private fun doAction(action: String) {
        when(action) {
            PlayerNotificationManager.ACTION_PLAY -> musicControllerUsecase.onPlay()
            PlayerNotificationManager.ACTION_PAUSE -> musicControllerUsecase.onPause()
            PlayerNotificationManager.ACTION_NEXT -> musicControllerUsecase.onNext()
            PlayerNotificationManager.ACTION_PREVIOUS -> musicControllerUsecase.onPrevious()
            PlayerNotificationManager.ACTION_STOP -> musicControllerUsecase.onStop()
            ACTION_QUIT -> quit()
        }
    }

    @UnstableApi
    override fun onUpdateNotification(session: MediaSession, startInForegroundRequired: Boolean) {
        // TODO: 이 메소드를 재정의하지않으면 2개의 Notification이 생기는 현상이 발생함. 원인을 찾아보자.
        Timber.d("onUpdateNotification")
    }

    @UnstableApi
    override fun onCreate() {
        Timber.tag(TAG).d( "onCreate")
        super.onCreate()
        playingMediaNotificationManager.startNotificationService(this, mediaSession)
        serviceScope.launch {
            playingMediaNotificationManager.cancelChannel.collectLatest {
                quit()
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.tag(TAG).d( "onTaskRemoved")
        quit()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.tag(TAG).d( "onLowMemory")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d( "onDestroy")
    }

    private fun quit() {
        Timber.tag(TAG).d( "quit")
        serviceScope.cancel()

        mediaSession.run {
            if (player.playbackState != Player.STATE_IDLE) {
                player.playWhenReady = false
                player.stop()
            }
//            try {
//                release()
//            } catch (e: Exception) {
//                return@run
//            }
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    companion object {
        const val MUSIC_STATE = "MusicState"
        const val MUSIC_DURATION = "MusicDuration"
        const val PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
        const val ACTION_QUIT = "$PACKAGE_NAME.quitservice"

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