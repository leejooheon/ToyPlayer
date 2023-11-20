package com.jooheon.clean_architecture.features.musicservice.notification

import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.google.common.collect.ImmutableList
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicservice.CustomMediaSessionCallback
import com.jooheon.clean_architecture.toyproject.features.musicservice.R

@UnstableApi
class CustomMediaNotificationProvider(
    private val context: Context,
    notificationIdProvider: NotificationIdProvider,
    channelId: String,
    channelNameResourceId: Int
) : DefaultMediaNotificationProvider(context, notificationIdProvider, channelId, channelNameResourceId) {

    override  fun  addNotificationActions (
        mediaSession: MediaSession,
        mediaButtons: ImmutableList<CommandButton>,
        builder: NotificationCompat.Builder,
        actionFactory: MediaNotification. ActionFactory
    ) : IntArray {
        val defaultPlayPauseCommandButton = mediaButtons.getOrNull( 0 )
        val notificationMediaButtons = if (defaultPlayPauseCommandButton != null ) {
            ImmutableList.builder<CommandButton>().apply {
                add(customRepeatButton(context))
                mediaButtons.forEach { add(it) }
                add(customShuffleButton(context))
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

    private fun customRepeatButton(context: Context): CommandButton {
        return CommandButton.Builder()
            .setDisplayName(UiText.StringResource(R.string.action_cycle_repeat).asString(context))
            .setSessionCommand(SessionCommand(CustomMediaSessionCallback.CYCLE_REPEAT, Bundle()))
            .setIconResId(R.drawable.ic_repeat)
            .build()
    }

    private fun customShuffleButton(context: Context): CommandButton {
        return CommandButton.Builder()
            .setDisplayName(UiText.StringResource(R.string.action_toggle_shuffle).asString(context))
            .setSessionCommand(SessionCommand(CustomMediaSessionCallback.TOGGLE_SHUFFLE, Bundle()))
            .setIconResId(R.drawable.ic_shuffle_off_circled)
            .build()
    }
}