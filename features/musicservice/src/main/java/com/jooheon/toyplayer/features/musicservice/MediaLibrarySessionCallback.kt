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
        if(session.isAutomotiveController(browser) || session.isAutoCompanionController(browser)) {
            val rootItem = mediaItemProvider.rootItem()
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED))
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onDisconnected(session, controller)
        Timber.d("onDisconnected")
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

    override fun onGetChildren(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> = scope.future {
        val childBrowsableItems = mediaItemProvider.getChildBrowsableItems(parentId)
        Timber.d("onGetChildren for $parentId, children: ${childBrowsableItems.map { it.mediaMetadata.title }}")

        if (childBrowsableItems.isNotEmpty()) {
            mediaItemProvider.setCurrentDisplayedSongList(childBrowsableItems)
            LibraryResult.ofItemList(childBrowsableItems, params)
        } else {
            LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        }
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> = scope.future {
        val item = mediaItemProvider.getItem(mediaId)
        Timber.d("onGetItem (mediaId = $mediaId, ${item?.mediaMetadata?.title})")
        if (item != null) {
            LibraryResult.ofItem(item, null)
        } else {
            LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        }
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        Timber.d("onSetMediaItems: ${mediaItems.map { it.mediaId }}")
        val item = mediaItems.singleOrNull()

        return if (startIndex == C.INDEX_UNSET && startPositionMs == C.TIME_UNSET && item != null) {
            scope.future {
                mediaItemProvider.mediaItemsWithStartPosition(item.mediaId) ?: run {
                    super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs).await()
                }
            }
        } else {
            super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs)
        }
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val items = mediaItems.map { item ->
            mediaItemProvider.getItem(item.mediaId) ?: item
        }.toMutableList()
        return Futures.immediateFuture(items)
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
}