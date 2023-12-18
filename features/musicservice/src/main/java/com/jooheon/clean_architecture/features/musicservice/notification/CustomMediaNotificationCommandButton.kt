package com.jooheon.clean_architecture.features.musicservice.notification

import android.content.Context
import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.RepeatMode
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.toyproject.features.musicservice.R

class CustomMediaNotificationCommandButton(
    customAction: String,
    displayName: String,
    iconResId: Int,
) {
    val commandButton: CommandButton
    init {
        commandButton = CommandButton.Builder()
            .setDisplayName(displayName)
            .setSessionCommand(SessionCommand(customAction, Bundle.EMPTY))
            .setIconResId(iconResId)
            .build()
    }

    companion object {
        fun shuffleButton(context: Context, shuffleMode: Boolean): CustomMediaNotificationCommandButton {
            val shuffleIconResId = when(shuffleMode) {
                true -> R.drawable.ic_shuffle_on_circled
                false -> R.drawable.ic_shuffle_off_circled
            }
            return CustomMediaNotificationCommandButton(
                customAction = MusicService.TOGGLE_SHUFFLE,
                displayName = context.getString(R.string.action_toggle_shuffle),
                iconResId = shuffleIconResId
            )
        }

        fun repeatButton(context: Context, @RepeatMode repeatMode: Int): CustomMediaNotificationCommandButton {
            val repeatIconResId = when(repeatMode) {
                REPEAT_MODE_ALL -> R.drawable.ic_repeat_white_circle
                REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
                REPEAT_MODE_OFF -> R.drawable.ic_repeat
                else -> throw IllegalStateException("Unknown RepeatMode: $repeatMode")
            }
            return CustomMediaNotificationCommandButton(
                customAction = MusicService.CYCLE_REPEAT,
                displayName = context.getString(R.string.action_cycle_repeat),
                iconResId = repeatIconResId
            )
        }
    }
}