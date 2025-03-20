package com.jooheon.toyplayer.features.musicservice.player

import android.os.Bundle
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
sealed interface CustomCommand {
    @Serializable
    data object CycleRepeat: CustomCommand

    @Serializable
    data object ToggleShuffle: CustomCommand

    companion object {
        const val CUSTOM_COMMAND_ACTION = "commandAction"
        internal const val CUSTOM_COMMAND_EXTRA = "commandExtra"

        internal fun parse(
            command: SessionCommand,
            args: Bundle,
        ): CustomCommand? {
            if (command.customAction != CUSTOM_COMMAND_ACTION) {
                return null
            }
            val json = args.getString(CUSTOM_COMMAND_EXTRA) ?: return null
            return Json.decodeFromString(serializer(), json)
        }
    }
    fun serialize() = Json.encodeToString(serializer(), this)
}

internal fun MediaController.sendCustomCommand(command: CustomCommand): ListenableFuture<SessionResult> {
    val json = Json.encodeToString(CustomCommand.serializer(), command)
    return sendCustomCommand(
        SessionCommand(CustomCommand.CUSTOM_COMMAND_ACTION, Bundle.EMPTY),
        Bundle().apply {
            putString(CustomCommand.CUSTOM_COMMAND_EXTRA, json)
        },
    )
}