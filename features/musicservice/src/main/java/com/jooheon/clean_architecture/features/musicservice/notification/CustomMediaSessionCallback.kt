package com.jooheon.clean_architecture.features.musicservice.notification

import android.content.Context
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.clean_architecture.features.musicservice.MusicService

class CustomMediaSessionCallback(
    private val context: Context,
    private val onCustomEvent: (CustomEvent) -> Unit,
): MediaLibrarySession.Callback {
    sealed class CustomEvent {
        data object OnRepeatIconPressed: CustomEvent()
        data object OnShuffleIconPressed: CustomEvent()
    }
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()

        CustomMediaNotificationCommandButton.shuffleButton(
            context = context,
            shuffleMode = session.player.shuffleModeEnabled,
        ).commandButton.sessionCommand?.let {
            availableSessionCommands.add(it)
        }

        CustomMediaNotificationCommandButton.repeatButton(
            context = context,
            repeatMode = session.player.repeatMode,
        ).commandButton.sessionCommand?.let {
            availableSessionCommands.add(it)
        }

        return MediaSession.ConnectionResult.accept(
            availableSessionCommands.build(), connectionResult.availablePlayerCommands
        )
    }

    @UnstableApi
    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            MusicService.CYCLE_REPEAT -> onCustomEvent(CustomEvent.OnRepeatIconPressed)
            MusicService.TOGGLE_SHUFFLE -> onCustomEvent(CustomEvent.OnShuffleIconPressed)
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        // The service currently does not support playback resumption. Tell System UI by returning
        // an error of type 'RESULT_ERROR_NOT_SUPPORTED' for a `params.isRecent` request. See
        return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED))
    }
}