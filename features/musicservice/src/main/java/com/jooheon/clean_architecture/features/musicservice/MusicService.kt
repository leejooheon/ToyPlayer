package com.jooheon.clean_architecture.features.musicservice

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import androidx.media3.ui.PlayerNotificationManager
import com.google.common.util.concurrent.MoreExecutors
import com.jooheon.clean_architecture.features.musicservice.notification.CoilBitmapLoader
import com.jooheon.clean_architecture.features.musicservice.notification.CustomMediaNotificationProvider
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUsecase
import com.jooheon.clean_architecture.toyproject.features.musicservice.BuildConfig
import com.jooheon.clean_architecture.toyproject.features.musicservice.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MusicService: MediaLibraryService() {
    private val TAG = MusicService::class.java.simpleName

    @Inject
    lateinit var musicControllerUsecase: MusicControllerUsecase

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var singleTopActivityIntent: Intent

    @Inject
    lateinit var mediaSessionCallback: CustomMediaSessionCallback
    private lateinit var mediaSession: MediaLibrarySession


    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = mediaSession

    @UnstableApi
    override fun onCreate() {
        Timber.tag(TAG).d( "onCreate")
        super.onCreate()
        initNotification()
        initMediaSession()

        val sessionToken = SessionToken(this, ComponentName(this, MusicService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({ controllerFuture.get() }, MoreExecutors.directExecutor())
    }


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
    private fun initNotification() {
        val mediaNotificationProvider = CustomMediaNotificationProvider(
            /** context **/this,
            { NOTIFICATION_ID },
            NOTIFICATION_CHANNEL_ID,
            R.string.playing_notification_name
        )

        setMediaNotificationProvider(mediaNotificationProvider)
    }

    @UnstableApi
    private fun initMediaSession() {
        mediaSession = MediaLibrarySession.Builder(
            this, exoPlayer, mediaSessionCallback
        ).setSessionActivity(
            PendingIntent.getActivity(
                this, 0, singleTopActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        ).setBitmapLoader(
            provideCoilBitmapLoader(this)
        ).build()
    }

    @UnstableApi
    fun provideCoilBitmapLoader(context: Context): CoilBitmapLoader = CoilBitmapLoader(context, serviceScope)

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Timber.tag(TAG).d( "onTaskRemoved")
        quit()
    }
    override fun onDestroy() {
        super.onDestroy()
        Timber.tag(TAG).d( "onDestroy")
        quit()
    }

    private fun quit() {
        Timber.tag(TAG).d( "quit")
        serviceScope.cancel()

        mediaSession.run {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.release()
            }
//            https://github.com/androidx/media/issues/389: 서비스 중지를 api에서 해준다구..??
        }

        stopSelf()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.tag(TAG).d( "onLowMemory")
    }

    companion object {
        const val MUSIC_STATE = "MusicState"
        const val MUSIC_DURATION = "MusicDuration"
        const val PACKAGE_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
        const val ACTION_QUIT = "$PACKAGE_NAME.quitservice"

        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"
        const val NOTIFICATION_CHANNEL_NAME = "Jooheon player notification"
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