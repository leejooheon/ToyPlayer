package com.jooheon.clean_architecture.features.musicservice.notification

import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.google.common.collect.ImmutableList
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.toyproject.features.musicservice.R

@UnstableApi
class CustomMediaNotificationProvider(
    private val context: Context,
    notificationIdProvider: NotificationIdProvider,
    channelId: String,
    channelNameResourceId: Int,
) : DefaultMediaNotificationProvider(context, notificationIdProvider, channelId, channelNameResourceId) {
    override fun addNotificationActions (
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification. ActionFactory
    ): IntArray {
        val defaultPlayPauseCommandButton = mediaButtons.getOrNull( 0 )
        val notificationMediaButtons = if (defaultPlayPauseCommandButton != null ) {
            ImmutableList.builder<CommandButton>().apply {
                add(CustomMediaNotificationCommandButton.repeatButton(context, RepeatMode.REPEAT_OFF).commandButton) // TODO: 업데이트 어떻게 할까?
                mediaButtons.forEach { add(it) }
                add(CustomMediaNotificationCommandButton.shuffleButton(context, ShuffleMode.SHUFFLE).commandButton) // TODO: 업데이트 어떻게 할까?
            }.build()
        } else {
            mediaButtons
        }

        return super.addNotificationActions(
            mediaSession,
            notificationMediaButtons,
            builder,
            actionFactory
        )
    }
}