package com.jooheon.toyplayer.features.musicservice

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.toyplayer.domain.common.extension.defaultZero
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.guava.future
import timber.log.Timber

@OptIn(UnstableApi::class)
class MediaLibrarySessionCallback(
    private val context: Context,
    private val mediaItemProvider: MediaItemProvider,
): MediaLibrarySession.Callback {
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    fun release() {
        scope.cancel()
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        Timber.d("onGetLibraryRoot (packageName: ${browser.packageName}), (isRecent = ${params?.isRecent == true}")

        return Futures.immediateFuture(LibraryResult.ofItem(mediaItemProvider.rootItem, params))
    }

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        // browsable mediaItem을 눌렀을 경우 호출
        return scope.future {
            Timber.d("onGetChildren: $parentId, $page, $pageSize")

            val items = mediaItemProvider.getChildMediaItems(parentId)
            val result = if (items.isEmpty()) LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                         else LibraryResult.ofItemList(items, params)

            return@future result
        }
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        Timber.d("onSetMediaItems: [$startIndex, $startPositionMs], ${mediaItems.map { Pair(it.mediaId, it.mediaMetadata.title) }}")
        val mediaItem = mediaItems.firstOrNull() ?: run {
            return super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs)
        }

        return scope.future {
            val (newMediaItems, index) = mediaItemProvider.getSongListOrNull(mediaItem)

            if(newMediaItems.isNullOrEmpty()) {
                MediaSession.MediaItemsWithStartPosition(mediaItems, startIndex, startPositionMs)
            } else {
                MediaSession.MediaItemsWithStartPosition(newMediaItems, index, C.TIME_UNSET)
            }
        }
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        Timber.d("onCustomCommand: ${customCommand.customAction}")
        when (customCommand.customAction) {
            MusicService.CYCLE_REPEAT -> {
                val value = (session.player.repeatMode.defaultZero() + 1) % 3
                session.player.repeatMode = value
            }
            MusicService.TOGGLE_SHUFFLE -> {
                session.player.shuffleModeEnabled = !(session.player.shuffleModeEnabled)
            }
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onPostConnect(session, controller)

        session.setCustomLayout(
            CustomMediaNotificationCommand.layout(
                context = context,
                shuffleMode = session.player.shuffleModeEnabled,
                repeatMode = session.player.repeatMode
            )
        )
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        Timber.d("onConnect: packageName: ${controller.packageName}")
        val connectionResult = super.onConnect(session, controller)
        val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            .add(SessionCommand(MusicService.TOGGLE_SHUFFLE, Bundle.EMPTY)).also {
                it.add(SessionCommand(MusicService.CYCLE_REPEAT, Bundle.EMPTY))
            }

        return MediaSession.ConnectionResult.accept(
            availableSessionCommands.build(), connectionResult.availablePlayerCommands
        )
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onDisconnected(session, controller)
        Timber.d("onDisconnected")
    }
}