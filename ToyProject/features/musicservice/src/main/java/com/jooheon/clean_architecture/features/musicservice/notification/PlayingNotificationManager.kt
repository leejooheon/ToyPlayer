package com.jooheon.clean_architecture.features.musicservice.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.jooheon.clean_architecture.features.musicservice.MediaSessionCallback
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.R
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.features.common.utils.VersionUtil

class PlayingNotificationManager(
    private val context: Context,
    private val rootActivityIntent: Intent,
    private val notificationManager: NotificationManager,
) {
    private val TAG = MusicService::class.java.simpleName + "@" + PlayingNotificationManager::class.java.simpleName
    companion object {
        const val NOTIFICATION_ID = 123
        const val NOTIFICATION_CHANNEL_ID = "bugs_lite_player_notification"
    }
    init {
        createNotificationChannel(context, notificationManager)
    }

    fun notificationMediaPlayer(
        context: Context,
        mediaStyle: androidx.media.app.NotificationCompat.MediaStyle,
        state: MusicState,
        bitmap: Bitmap? = null
    ): Notification {
        val actions = notificationActions(state)
        val largeIcon = bitmap ?: run {
            context.getDrawable(
                com.jooheon.clean_architecture.features.common.R.drawable.ic_placeholder
            )?.toBitmap()
        }

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setStyle(mediaStyle)

            setContentTitle(state.currentPlayingMusic.title)
            setContentText(state.currentPlayingMusic.album)
            setSubText(state.currentPlayingMusic.artist)

            setDeleteIntent(retrievePlaybackAction(MediaSessionCallback.ACTION_QUIT))
            setContentIntent(getClickIntent())

            setLargeIcon(largeIcon)
            setSmallIcon(R.drawable.ic_notification)

            setOnlyAlertOnce(true)

            actions.forEach { addAction(it) }
        }.build()
    }

    private fun notificationActions(state: MusicState): List<NotificationCompat.Action> {
        val playPauseAction = NotificationCompat.Action(
            if (state.isPlaying) R.drawable.ic_pause
            else R.drawable.ic_play_arrow,
            context.getString(R.string.action_play_pause),
            retrievePlaybackAction(MediaSessionCallback.ACTION_PLAY_PAUSE)
        )

        val quitAction = NotificationCompat.Action(
            R.drawable.ic_clear_24,
            context.getString(R.string.action_quit),
            retrievePlaybackAction(MediaSessionCallback.ACTION_QUIT)
        )

        val previousAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous,
            context.getString(R.string.action_previous),
            retrievePlaybackAction(MediaSessionCallback.ACTION_PREVIOUS)
        )

        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next,
            context.getString(R.string.action_next),
            retrievePlaybackAction(MediaSessionCallback.ACTION_NEXT)
        )

        val list = mutableListOf<NotificationCompat.Action>().apply {
            add(previousAction)
            add(playPauseAction)
            add(nextAction)
            add(quitAction)
        }

        return list
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(26)
    private fun createNotificationChannel(
        context: Context,
        notificationManager: NotificationManager
    ) {
        var notificationChannel: NotificationChannel? = notificationManager.getNotificationChannel(
            NOTIFICATION_CHANNEL_ID
        )

        if (notificationChannel == null) {
            notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.playing_notification_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.playing_notification_description)
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                setBypassDnd(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setAllowBubbles(false)
                }
            }

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun getClickIntent(): PendingIntent {
        val clickIntent = PendingIntent.getActivity(
            context,
            0,
            rootActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return clickIntent
    }
}