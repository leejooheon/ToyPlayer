package com.jooheon.toyplayer.features.musicservice.notification

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.Player.RepeatMode
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.jooheon.toyplayer.features.musicservice.MusicService
import com.jooheon.toyplayer.features.musicservice.R
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import timber.log.Timber

class CustomMediaNotificationCommand(
    command: SessionCommand,
    displayName: String,
    iconResId: Int,
) {
    val commandButton = CommandButton.Builder()
        .setDisplayName(displayName)
        .setSessionCommand(command)
        .setIconResId(iconResId)
        .build()

    companion object {
        fun layout(context: Context, shuffleMode: Boolean, @RepeatMode repeatMode: Int): List<CommandButton> {
            Timber.d("update layout: shuffleMode - $shuffleMode, repeatMode - $repeatMode")
            val shuffleButton = shuffleButton(
                context = context,
                shuffleMode = shuffleMode,
            ).commandButton
            val repeatButton = repeatButton(
                context = context,
                repeatMode = repeatMode,
            ).commandButton

            return listOf(shuffleButton, repeatButton)
        }
        private fun shuffleButton(context: Context, shuffleMode: Boolean): CustomMediaNotificationCommand {
            val shuffleIconResId = when(shuffleMode) {
                true -> R.drawable.ic_shuffle_on_circled
                false -> R.drawable.ic_shuffle_off_circled
            }
            return CustomMediaNotificationCommand(
                command = SessionCommand(
                    CustomCommand.CUSTOM_COMMAND_ACTION,
                    bundleOf(CustomCommand.CUSTOM_COMMAND_EXTRA to CustomCommand.ToggleShuffle.serialize())
                ),
                displayName = context.getString(R.string.action_toggle_shuffle),
                iconResId = shuffleIconResId
            )
        }

        private fun repeatButton(context: Context, @RepeatMode repeatMode: Int): CustomMediaNotificationCommand {
            val repeatIconResId = when(repeatMode) {
                REPEAT_MODE_ALL -> R.drawable.ic_repeat_white_circle
                REPEAT_MODE_ONE -> R.drawable.ic_repeat_one
                REPEAT_MODE_OFF -> R.drawable.ic_repeat
                else -> throw IllegalStateException("Unknown RepeatMode: $repeatMode")
            }
            return CustomMediaNotificationCommand(
                command = SessionCommand(
                    CustomCommand.CUSTOM_COMMAND_ACTION,
                    bundleOf(CustomCommand.CUSTOM_COMMAND_EXTRA to CustomCommand.CycleRepeat.serialize())
                ),
                displayName = context.getString(R.string.action_cycle_repeat),
                iconResId = repeatIconResId
            )
        }
    }
}