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
    private val musicState: MusicState,
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
                add(CustomMediaNotificationCommandButton.repeatButton(context, musicState.repeatMode).commandButton)
                mediaButtons.forEach { add(it) }
                add(CustomMediaNotificationCommandButton.shuffleButton(context, musicState.shuffleMode).commandButton)
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