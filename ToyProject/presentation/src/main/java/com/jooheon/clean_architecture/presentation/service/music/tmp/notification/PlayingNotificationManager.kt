package com.jooheon.clean_architecture.presentation.service.music.tmp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import com.jooheon.clean_architecture.presentation.MainActivity
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.utils.VersionUtils
import androidx.core.app.NotificationCompat
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.service.music.extensions.MusicState
import com.jooheon.clean_architecture.presentation.service.music.tmp.notification.MediaButtonIntentReceiver.Companion.ACTION_QUIT

class PlayingNotificationManager(
    private val context: Context,
    notificationManager: NotificationManager
) {
    companion object {
        const val NOTIFICATION_ID = 123
        const val NOTIFICATION_CHANNEL_ID = "jh_player_notification"
    }
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, notificationManager)
        }
    }

    fun foregroundNotification(): Notification {
        val clickIntent = getClickIntent()
        val deleteIntent = getDeleteIntent()

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle("ContentTitle(Title)")
            setContentText("ContentText(ArtistName)")
            setSubText("SubText(Album)")
            setCategory(Notification.CATEGORY_SERVICE)
            setContentIntent(clickIntent)
            setDeleteIntent(deleteIntent)
        }.build()

        /**
         * Set Action!!
         * perv, next, play, pause etc...
         */

        return notification
    }


    fun notificationMediaPlayer(
        context: Context,
        mediaStyle: androidx.media.app.NotificationCompat.MediaStyle,
        state: MusicState
    ): Notification {
        val playPauseAction = NotificationCompat.Action(
            if (state.isPlaying) R.drawable.ic_pause
            else R.drawable.ic_play_arrow,
            "PlayPause",
            retrievePlaybackAction(
                if (state.isPlaying) SongAction.PAUSE.ordinal.toString()
                else SongAction.RESUME.ordinal.toString()
            )
        )

        val previousAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous,
            "Previous",
            retrievePlaybackAction(SongAction.PREVIOUS.ordinal.toString())
        )

        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next,
            "Previous",
            retrievePlaybackAction(SongAction.NEXT.ordinal.toString())
        )

        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setStyle(mediaStyle)
            .setSmallIcon(R.drawable.ic_play_arrow)
            .setOnlyAlertOnce(true)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .build()
    }

    private fun retrievePlaybackAction(action: String): PendingIntent {
        val serviceName = ComponentName(context, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or
                    if (VersionUtils.hasMarshmallow()) PendingIntent.FLAG_IMMUTABLE
                    else 0
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
        val action = Intent(context, MainActivity::class.java)
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        val clickIntent = PendingIntent.getActivity(
            context,
            0,
            action,
            getIntentFlags()
        )

        return clickIntent
    }

    private fun getDeleteIntent(): PendingIntent {
        val intent = Intent(ACTION_QUIT).apply {
            component = ComponentName(context, MusicService::class.java)
        }

        val deleteIntent = PendingIntent.getService(
            context,
            0,
            intent,
            getIntentFlags()
        )

        return deleteIntent
    }

    private fun getIntentFlags(): Int {
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or if (VersionUtils.hasMarshmallow()) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        return flags
    }
}