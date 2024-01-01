package com.jooheon.clean_architecture.features.musicservice

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
import com.jooheon.clean_architecture.features.musicservice.data.MediaItemProvider
import com.jooheon.clean_architecture.features.musicservice.notification.CustomMediaNotificationCommandButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.guava.future
import timber.log.Timber


@OptIn(UnstableApi::class)
class CustomMediaSessionCallback(
    private val context: Context,
    private val mediaItemProvider: MediaItemProvider,
): MediaLibrarySession.Callback {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var onCustomEvent: ((CustomEvent) -> Unit)? = null
    sealed class CustomEvent {
        data object OnRepeatIconPressed: CustomEvent()
        data object OnShuffleIconPressed: CustomEvent()
    }
    fun initEventListener(listener: (CustomEvent) -> Unit) {
        onCustomEvent = listener
    }
    fun release() {
        onCustomEvent = null
        scope.cancel()
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        Timber.d("onConnect: packageName: ${controller.packageName}")

        if(!MusicService.allowedCaller(controller.packageName)) {
            return MediaSession.ConnectionResult.reject()
        }

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

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        when (customCommand.customAction) {
            MusicService.CYCLE_REPEAT -> onCustomEvent?.invoke(CustomEvent.OnRepeatIconPressed)
            MusicService.TOGGLE_SHUFFLE -> onCustomEvent?.invoke(CustomEvent.OnShuffleIconPressed)
        }

        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        if(!MusicService.allowedCaller(browser.packageName)) {
            Timber.d("onGetLibraryRoot - not allowed caller: ${browser.packageName}")
            return Futures.immediateFuture(LibraryResult.ofError(LibraryResult.RESULT_ERROR_NOT_SUPPORTED))
        }

        val rootItem = mediaItemProvider.rootItem()
        Timber.d("onGetLibraryRoot (isRecent = ${params?.isRecent == true}, rootItemId = ${rootItem.mediaId})")
        return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
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
        Timber.d("onGetChildren for $parentId, children: ${childBrowsableItems.size}")
        if (childBrowsableItems.isNotEmpty()) {
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
        Timber.d("onGetItem")
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
        val item = mediaItems.singleOrNull()
        return if (startIndex == C.INDEX_UNSET && startPositionMs == C.TIME_UNSET && item != null) {
            scope.future {
                mediaItemProvider.mediaItemsWithStartPosition(item.mediaId)
                    ?: super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs).await()
            }
        } else {
            super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs)
        }
    }
}