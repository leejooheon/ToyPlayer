package com.jooheon.clean_architecture.features.musicservice.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.clean_architecture.features.musicservice.MusicService
import com.jooheon.clean_architecture.features.musicservice.usecase.MusicControllerUseCase
import com.jooheon.clean_architecture.toyproject.features.musicservice.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class CustomMediaSessionCallback(
    private val context: Context,
    private val musicControllerUseCase: MusicControllerUseCase,
): MediaLibrarySession.Callback {

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        val state = musicControllerUseCase.musicState.value
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()

        CustomMediaNotificationCommandButton.repeatButton(
            context = context,
            repeatMode = state.repeatMode
        ).commandButton.sessionCommand?.let {
            availableSessionCommands.add(it)
        }

        CustomMediaNotificationCommandButton.shuffleButton(
            context = context,
            shuffleMode = state.shuffleMode
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
            MusicService.CYCLE_REPEAT -> musicControllerUseCase.onRepeatButtonPressed()
            MusicService.TOGGLE_SHUFFLE -> musicControllerUseCase.onShuffleButtonPressed()
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }
}