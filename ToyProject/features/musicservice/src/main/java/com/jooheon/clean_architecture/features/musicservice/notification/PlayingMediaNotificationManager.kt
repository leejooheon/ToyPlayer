package com.jooheon.clean_architecture.features.musicservice.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.toyproject.features.musicservice.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

class PlayingMediaNotificationManager(
    @ApplicationContext private val context: Context,
    private val applicationScope: CoroutineScope,
    private val player: ExoPlayer,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + PlayingMediaNotificationManager::class.java.simpleName
    companion object {
        const val NOTIFICATION_ID = 234
        const val NOTIFICATION_CHANNEL_ID = "Jooheon_player_notification"
        const val NOTIFICATION_CHANNEL_NAME = "Jooheon player notification"
    }

    private var notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private val _cancelChannel = Channel<Unit>()
    val cancelChannel = _cancelChannel.receiveAsFlow()

    @UnstableApi
    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        buildNotification(mediaSession)
        startForegroundNotification(mediaSessionService)
    }

    @UnstableApi
    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(
                PlayingMediaNotificationAdapter(
                    context = context,
                    applicationScope = applicationScope,
                    pendingIntent = mediaSession.sessionActivity,
                )
            )
            .setPlayActionIconResourceId(R.drawable.ic_play_arrow)
            .setPauseActionIconResourceId(R.drawable.ic_pause)
            .setNextActionIconResourceId(R.drawable.ic_skip_next)
            .setPreviousActionIconResourceId(R.drawable.ic_skip_previous)
            .setStopActionIconResourceId(R.drawable.ic_clear_24)
            .setSmallIconResourceId(R.drawable.ic_notification)
            .setNotificationListener(notificationListener)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)

                it.setUsePlayPauseActions(true)
                it.setUsePreviousAction(true)
                it.setUseNextAction(true)
                it.setUseRewindAction(true)
                it.setUseStopAction(true)

                it.setUseFastForwardAction(false)
                it.setUseRewindAction(false)
                it.setUseFastForwardActionInCompactView(false)
                it.setUseRewindActionInCompactView(false)

                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(player)
            }
    }

    @UnstableApi
    private val notificationListener = object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            Timber.tag(TAG).d("onNotificationCancelled id: $notificationId, dismissedByUser: $dismissedByUser")
            _cancelChannel.trySend(Unit)
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            super.onNotificationPosted(notificationId, notification, ongoing)
//            Timber.tag(TAG).d("onNotificationPosted id: $notificationId, ongoing: $ongoing")
        }
    }

    private fun startForegroundNotification(mediaSessionService: MediaSessionService) {
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_TRANSPORT)
//            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}