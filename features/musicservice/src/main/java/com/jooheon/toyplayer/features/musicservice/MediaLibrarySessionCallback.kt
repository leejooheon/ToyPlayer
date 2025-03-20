package com.jooheon.toyplayer.features.musicservice

import android.content.Context
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.os.bundleOf
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.common.extension.default
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.MediaId.Companion.toMediaIdOrNull
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Playlist.Companion.RadioPlaylistId
import com.jooheon.toyplayer.domain.usecase.PlaybackSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.extension.getDefaultPlaylistName
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationCommand
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.future
import timber.log.Timber

@OptIn(UnstableApi::class)
class MediaLibrarySessionCallback(
    private val context: Context,
    private val scope: CoroutineScope,
    private val mediaItemProvider: MediaItemProvider,
    private val playlistUseCase: PlaylistUseCase,
    private val playbackSettingsUseCase: PlaybackSettingsUseCase,
): MediaLibrarySession.Callback {
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

            if (parentId.toMediaIdOrNull() == MediaId.Root) {
                initDefaultPlaylist()
            }

            val items = mediaItemProvider.getChildMediaItems(parentId)
            val result = if (items.isEmpty()) LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                         else LibraryResult.ofItemList(items, params)

            return@future result
        }
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        Timber.d("onAddMediaItems: ${mediaItems.map{it.mediaId}}")
        return super.onAddMediaItems(mediaSession, controller, mediaItems)
    }

    override fun onSetMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        Timber.d("onSetMediaItems: [$startIndex, $startPositionMs], ${mediaItems.map { Pair(it.mediaId, it.mediaMetadata.title) }}")
        return super.onSetMediaItems(mediaSession, controller, mediaItems, startIndex, startPositionMs)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        val command = CustomCommand.parse(customCommand, args)
        Timber.d("onCustomCommand: $command")
        return scope.future {
            when (command) {
                is CustomCommand.CycleRepeat -> {
                    val repeatMode = (session.player.repeatMode.defaultZero() + 1) % 3
                    session.player.repeatMode = repeatMode
                    SessionResult(SessionResult.RESULT_SUCCESS)
                }
                CustomCommand.ToggleShuffle -> {
                    session.player.shuffleModeEnabled = !(session.player.shuffleModeEnabled)
                    SessionResult(SessionResult.RESULT_SUCCESS)
                }
                else -> SessionResult(SessionError.ERROR_NOT_SUPPORTED)
            }
        }
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
        val sessionCommands = connectionResult.availableSessionCommands
            .buildUpon()
            .add(
                SessionCommand(
                    CustomCommand.CUSTOM_COMMAND_ACTION,
                    Bundle.EMPTY
                )
            ) // enable custom action
            .build()

        return MediaSession.ConnectionResult.accept(
            sessionCommands, connectionResult.availablePlayerCommands
        )
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        super.onDisconnected(session, controller)
        Timber.d("onDisconnected")
    }

    internal fun invalidateCustomLayout(session: MediaSession?) {
        session ?: return
        session.setCustomLayout(
            CustomMediaNotificationCommand.layout(
                context = context,
                shuffleMode = session.player.shuffleModeEnabled,
                repeatMode = session.player.repeatMode
            )
        )
    }

    private suspend fun initDefaultPlaylist() {
        Playlist.defaultPlaylistIds.forEach { (id, mediaId) ->
            val songs = mediaItemProvider.getChildMediaItems(mediaId.serialize()).map { it.toSong() }
            playlistUseCase.getPlaylist(id)
                .onSuccess {
                    if(it.id == Playlist.LocalPlaylistId.first) {
                        playlistUseCase.updatePlaylists(it.copy(songs = songs))
                    }
                }
                .onError {
                    val playlist = Playlist(
                        id = id,
                        name = getDefaultPlaylistName(context, mediaId),
                        thumbnailUrl = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                        songs = songs
                    )
                    playlistUseCase.insertPlaylists(playlist)
                }
        }
    }
}