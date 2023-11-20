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
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
import com.jooheon.clean_architecture.features.essential.base.UiText
import com.jooheon.clean_architecture.features.musicservice.CustomMediaSessionCallback
import com.jooheon.clean_architecture.features.musicservice.data.MusicState
import com.jooheon.clean_architecture.toyproject.features.musicservice.R

@UnstableApi
class CustomMediaNotificationProvider(
    private val context: Context,
    notificationIdProvider: NotificationIdProvider,
    channelId: String,
    channelNameResourceId: Int,
    private val musicState: MusicState
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
        val repeatIconResId = when(musicState.repeatMode) {
            RepeatMode.REPEAT_ALL -> R.drawable.ic_repeat_white_circle
            RepeatMode.REPEAT_ONE -> R.drawable.ic_repeat_one
            RepeatMode.REPEAT_OFF -> R.drawable.ic_repeat
        }
        return CommandButton.Builder()
            .setDisplayName(context.getString(R.string.action_cycle_repeat))
            .setSessionCommand(SessionCommand(CustomMediaSessionCallback.CYCLE_REPEAT, Bundle()))
            .setIconResId(repeatIconResId)
            .build()
    }

    private fun customShuffleButton(context: Context): CommandButton {
        val shuffleIconResId = when(musicState.shuffleMode) {
            ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_on_circled
            ShuffleMode.NONE -> R.drawable.ic_shuffle_off_circled
        }

        return CommandButton.Builder()
            .setDisplayName(context.getString(R.string.action_toggle_shuffle))
            .setSessionCommand(SessionCommand(CustomMediaSessionCallback.TOGGLE_SHUFFLE, Bundle()))
            .setIconResId(shuffleIconResId)
            .build()
    }
}