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
    context: Context,
    notificationIdProvider: NotificationIdProvider,
    channelId: String,
    channelNameResourceId: Int,
) : DefaultMediaNotificationProvider(context, notificationIdProvider, channelId, channelNameResourceId) {
    override fun getMediaButtons(
        session: MediaSession,
        playerCommands: Player.Commands,
        customLayout: ImmutableList<CommandButton>,
        showPauseButton: Boolean,
    ): ImmutableList<CommandButton> {
        return super.getMediaButtons(session, playerCommands, customLayout, showPauseButton)
            .apply {
                forEachIndexed { index, commandButton ->
                    // This shows the previous / next icons in compact mode for Android < 13
                    commandButton?.extras?.putInt(COMMAND_KEY_COMPACT_VIEW_INDEX, index)
                }
            }
    }
}