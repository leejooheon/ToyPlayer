package com.jooheon.toyplayer.features.musicservice

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
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
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.core.resources.UiText
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.common.onError
import com.jooheon.toyplayer.domain.model.common.onSuccess
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.MediaId.Companion.toMediaIdOrNull
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.permission.audioStoragePermission
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.ext.findExoPlayer
import com.jooheon.toyplayer.features.musicservice.ext.forceEnqueue
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.notification.CustomMediaNotificationCommand
import com.jooheon.toyplayer.features.musicservice.player.CustomCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(UnstableApi::class)
class MediaLibrarySessionCallback(
    private val context: Context,
    private val scope: CoroutineScope,
    private val mediaItemProvider: MediaItemProvider,
    private val playlistUseCase: PlaylistUseCase,
    private val playerSettingsUseCase: PlayerSettingsUseCase,
    private val defaultSettingsUseCase: DefaultSettingsUseCase,
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
                launch { initDefaultPlaylist() }
            }

            val result = mediaItemProvider.getChildMediaItems(parentId)

            return@future when(result) {
                is Result.Success -> {
                    val items = result.data
                    if (items.isEmpty()) LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                    else LibraryResult.ofItemList(items, params)
                }
                is Result.Error -> {
                    val libraryParams = MediaLibraryService.LibraryParams.Builder()
                        .setExtras(
                            bundleOf(
                                PlaybackDataError.KEY_MESSAGE to result.error.serialize()
                            )
                        )
                        .build()
                    LibraryResult.ofError(SessionError.ERROR_BAD_VALUE, libraryParams)
                }
            }
        }
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        Timber.d("onGetItem")
        return super.onGetItem(session, browser, mediaId)
    }

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        Timber.d("onAddMediaItems: ${mediaItems.map{ it.mediaId }}")
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

        val mediaItem = mediaItems.singleOrNull() ?: run {
            return super.onSetMediaItems(
                mediaSession,
                controller,
                mediaItems,
                startIndex,
                startPositionMs
            )
        }

        val mediaId = mediaItem.mediaId.toMediaIdOrNull() as? MediaId.Playback ?: run {
            return super.onSetMediaItems(
                mediaSession,
                controller,
                mediaItems,
                startIndex,
                startPositionMs
            )
        }

        if(startIndex != C.INDEX_UNSET && startPositionMs != C.TIME_UNSET) {
            return super.onSetMediaItems(
                mediaSession,
                controller,
                mediaItems,
                startIndex,
                startPositionMs
            )
        }

        return scope.future {
            val result = mediaItemProvider.getChildMediaItems(mediaId.parentId)

            when(result) {
                is Result.Success -> {
                    val newMediaItems = result.data
                    val newStartIndex = newMediaItems
                        .indexOfFirst {
                            val targetMediaIdOrNull = it.mediaId.toMediaIdOrNull() as? MediaId.Playback
                            targetMediaIdOrNull?.id == mediaId.id
                        }
                        .defaultZero()
                    MediaSession.MediaItemsWithStartPosition(newMediaItems, newStartIndex, startPositionMs)
                }
                is Result.Error -> {
                    MediaSession.MediaItemsWithStartPosition(emptyList(), C.INDEX_UNSET, C.TIME_UNSET)
                }
            }
        }
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
                is CustomCommand.PrepareRecentQueue -> {
                    val result = prepareRecentQueue()
                    when(result) {
                        is Result.Success -> {
                            val (mediaItems, startIndex) = result.data
                            session.player.forceEnqueue(
                                mediaItems = mediaItems,
                                startPositionMs = 0L,
                                startIndex = startIndex,
                                playWhenReady = command.playWhenReady,
                            )
                            SessionResult(SessionResult.RESULT_SUCCESS)
                        }
                        is Result.Error -> SessionResult(SessionError.ERROR_NOT_SUPPORTED)
                    }
                }
                is CustomCommand.GetAudioSessionId -> {
                    session.player.findExoPlayer()?.let {
                        SessionResult(
                            SessionResult.RESULT_SUCCESS,
                            bundleOf(CustomCommand.GetAudioSessionId.KEY to it.audioSessionId)
                        )
                    } ?: run {
                        SessionResult(SessionError.ERROR_NOT_SUPPORTED)
                    }

                    SessionResult(SessionResult.RESULT_SUCCESS)
                }
                is CustomCommand.CycleRepeat -> {
                    val repeatMode = (session.player.repeatMode.defaultZero() + 1) % 3
                    playerSettingsUseCase.setRepeatMode(repeatMode)
                    SessionResult(SessionResult.RESULT_SUCCESS)
                }
                CustomCommand.ToggleShuffle -> {
                    playerSettingsUseCase.setShuffleMode(!(session.player.shuffleModeEnabled))
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

    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        return scope.future {
            Timber.d("onPlaybackResumption")
            val result = prepareRecentQueue()
            when(result) {
                is Result.Success -> {
                    val (mediaItems, startIndex) = result.data
                    MediaSession.MediaItemsWithStartPosition(
                        mediaItems,
                        startIndex,
                        0
                    )
                }
                is Result.Error -> throw UnsupportedOperationException()
            }
        }
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): MediaSession.ConnectionResult {
        Timber.d("onConnect: packageName: ${controller.packageName}, ${session.player.playbackState.toString()}")
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

    private suspend fun prepareRecentQueue(): Result<Pair<List<MediaItem>, Int>, PlaybackDataError> {
        val playlistResult = playlistUseCase.getPlayingQueue()
            .takeIf { it is Result.Success && it.data.songs.isNotEmpty() }
            ?: playlistUseCase.getPlaylist(Playlist.Radio.id)

        return when(playlistResult) {
            is Result.Success -> {
                val lastPlayedMediaId = defaultSettingsUseCase.lastPlayedMediaId().toMediaIdOrNull() as? MediaId.Playback
                val playlist = playlistResult.data
                defaultSettingsUseCase.setLastEnqueuedPlaylistName(playlist.name)

                val mediaItems = playlist.songs.map { it.toMediaItem(MediaId.Playlist(Playlist.PlayingQueue.id).serialize()) }
                val index = if(lastPlayedMediaId == null) 0
                else {
                    mediaItems
                        .indexOfFirst { (it.mediaId.toMediaIdOrNull() as? MediaId.Playback)?.id == lastPlayedMediaId.id }
                        .takeIf { it != C.INDEX_UNSET }
                        .defaultZero()
                }
                Timber.e("prepareRecentQueue: $lastPlayedMediaId, [${mediaItems.map { (it.mediaId.toMediaIdOrNull() as? MediaId.Playback)?.id }} / $index]")
                Result.Success(mediaItems to index)
            }

            is Result.Error -> playlistResult
        }
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

    private suspend fun initDefaultPlaylist() = withContext(Dispatchers.IO) {
        fun getInternalMediaId(mediaId: MediaId.Playlist): MediaId {
            val mappedMediaId = when (mediaId) {
                Playlist.PlayingQueue,
                Playlist.Favorite -> mediaId

                Playlist.Radio -> MediaId.InternalMediaId.RadioSongs
                Playlist.Stream -> MediaId.InternalMediaId.StreamSongs
                Playlist.Local -> MediaId.InternalMediaId.LocalSongs

                else -> throw IllegalArgumentException("Invalid playlist id: ${mediaId.id}")
            }

            return mappedMediaId
        }

        fun getDefaultPlaylistName(context: Context, mediaId: MediaId.Playlist): String {
            val resource =  when(mediaId) {
                Playlist.PlayingQueue -> Strings.playlist_playing_queue
                Playlist.Radio -> Strings.playlist_radio
                Playlist.Local -> Strings.playlist_local
                Playlist.Stream -> Strings.playlist_stream
                Playlist.Favorite -> Strings.playlist_favorite
                else -> throw IllegalArgumentException("$mediaId is not default playlist.")
            }

            return UiText.StringResource(resource).asString(context)
        }

        Playlist.defaultPlaylists
            .forEach { playlistMediaId ->
                val mediaId = getInternalMediaId(playlistMediaId)
                val result = mediaItemProvider.getChildMediaItems(mediaId.serialize())

                val songs = when(result) {
                    is Result.Success -> result.data.map { it.toSong() }
                    is Result.Error -> emptyList()
                }

                playlistUseCase.getPlaylist(playlistMediaId.id)
                    .onSuccess { playlist ->
                        playlistUseCase.updateName(
                            id = playlist.id,
                            name = getDefaultPlaylistName(context, playlistMediaId)
                        )

                        if(playlistMediaId == Playlist.Local) { // local은 앱 실행시마다 리로드 해준다
                            val granted = ContextCompat.checkSelfPermission(
                                context, audioStoragePermission
                            ) == PackageManager.PERMISSION_GRANTED

                            if(granted) {
                                // local song은 고유한 audioId를 갖는다.
                                val newSongs = songs.filter { localSong -> // DB에 이미 존재하지 않는 음악만 필터링
                                    localSong.audioId !in playlist.songs.map { song -> song.audioId } &&
                                    localSong.path != playlist.songs.find { it.audioId == localSong.audioId }?.path
                                }

                                playlistUseCase.insert(
                                    id = playlist.id,
                                    songs = newSongs,
                                    reset = false
                                )
                            } else {
                                playlistUseCase.insert(
                                    id = playlist.id,
                                    songs = emptyList(),
                                    reset = true
                                )
                            }
                        }
                    }
                    .onError {
                        val playlist = Playlist(
                            id = playlistMediaId.id,
                            name = getDefaultPlaylistName(context, playlistMediaId),
                            thumbnailUrl = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                        )
                        playlistUseCase.makeDefaultPlaylist(playlistMediaId.id, playlist, songs)
                    }
            }
    }
}