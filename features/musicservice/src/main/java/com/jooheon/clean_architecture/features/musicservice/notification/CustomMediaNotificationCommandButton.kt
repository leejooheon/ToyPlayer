package com.jooheon.clean_architecture.features.musicservice.notification

import android.content.Context
import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import com.jooheon.clean_architecture.domain.entity.music.RepeatMode
import com.jooheon.clean_architecture.domain.entity.music.ShuffleMode
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
            .setSessionCommand(SessionCommand(customAction, Bundle()))
            .setIconResId(iconResId)
            .build()
    }

    companion object {
        fun shuffleButton(context: Context, shuffleMode: ShuffleMode): CustomMediaNotificationCommandButton {
            val shuffleIconResId = when(shuffleMode) {
                ShuffleMode.SHUFFLE -> R.drawable.ic_shuffle_on_circled
                ShuffleMode.NONE -> R.drawable.ic_shuffle_off_circled
            }
            return CustomMediaNotificationCommandButton(
                customAction = MusicService.TOGGLE_SHUFFLE,
                displayName = context.getString(R.string.action_toggle_shuffle),
                iconResId = shuffleIconResId
            )
        }

        fun repeatButton(context: Context, repeatMode: RepeatMode): CustomMediaNotificationCommandButton {
            val repeatIconResId = when(repeatMode) {
                RepeatMode.REPEAT_ALL -> R.drawable.ic_repeat_white_circle
                RepeatMode.REPEAT_ONE -> R.drawable.ic_repeat_one
                RepeatMode.REPEAT_OFF -> R.drawable.ic_repeat
            }
            return CustomMediaNotificationCommandButton(
                customAction = MusicService.CYCLE_REPEAT,
                displayName = context.getString(R.string.action_cycle_repeat),
                iconResId = repeatIconResId
            )
        }
    }
}