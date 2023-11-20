package com.jooheon.clean_architecture.features.musicservice

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaBrowser
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
    private val TAG = MusicService::class.java.simpleName + "@" + "Main"

    @Inject
    lateinit var musicControllerUsecase: MusicControllerUsecase

    @Inject
    lateinit var singleTopActivityIntent: Intent

    @Inject
    lateinit var mediaSessionCallback: CustomMediaSessionCallback

    @Inject
    lateinit var exoPlayer: ExoPlayer

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
    }

    @UnstableApi
    override fun onDestroy() {
        Timber.tag(TAG).d( "onDestroy")
        musicControllerUsecase.releaseMediaBrowser()
        exoPlayer.release()
        mediaSession.release()
        serviceScope.cancel()
        super.onDestroy()
    }

    @UnstableApi
    private fun initNotification() {
        val mediaNotificationProvider = CustomMediaNotificationProvider(
            context = this,
            notificationIdProvider = { NOTIFICATION_ID },
            channelId = NOTIFICATION_CHANNEL_ID,
            channelNameResourceId = R.string.playing_notification_name
        )

        setMediaNotificationProvider(mediaNotificationProvider)
    }

    @UnstableApi
    private fun initMediaSession() {
        mediaSession = MediaLibrarySession.Builder(
            this, customForwardingPlayer, mediaSessionCallback
        ).setSessionActivity(
            PendingIntent.getActivity(
                this, 0, singleTopActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        ).setBitmapLoader(
            CoilBitmapLoader(this, serviceScope)
        ).build()
    }

    private val customForwardingPlayer by lazy {
        @UnstableApi
        object : ForwardingPlayer(exoPlayer) {
            override fun getAvailableCommands(): Player.Commands {
                return super.getAvailableCommands().buildUpon()
                    .add(Player.COMMAND_SEEK_TO_NEXT)
                    .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                    .remove(Player.COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM)
                    .build()
            }

            override fun isCommandAvailable(command: Int): Boolean {
                // https://github.com/androidx/media/issues/140
                val available = when(command) {
                    COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM -> false
                    COMMAND_SEEK_TO_NEXT -> true
                    COMMAND_SEEK_TO_PREVIOUS -> true
                    else -> super.isCommandAvailable(command)
                }

                return available
            }

            override fun play() {
                if(fromSystemUi()) musicControllerUsecase.onPlay()
                else super.play()
            }
            override fun pause() {
                if(fromSystemUi()) musicControllerUsecase.onPause()
                else super.pause()
            }
            override fun seekToNext() {
                if(fromSystemUi()) musicControllerUsecase.onNext()
                else super.seekToNext()
            }
            override fun seekToPrevious() {
                if(fromSystemUi()) musicControllerUsecase.onPrevious()
                else super.seekToPrevious()
            }
            override fun stop() {
                if(fromSystemUi()) musicControllerUsecase.onStop()
                else super.stop()
            }

            fun fromSystemUi() = mediaSession.isMediaNotificationController(mediaSession.controllerForCurrentRequest!!)
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
        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"
    }
}