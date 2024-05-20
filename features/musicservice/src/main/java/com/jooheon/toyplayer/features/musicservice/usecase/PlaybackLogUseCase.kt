package com.jooheon.toyplayer.features.musicservice.usecase

import android.content.Context
import androidx.media3.common.C
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.observer.NetworkConnectivityObserver
import com.jooheon.toyplayer.features.common.extension.showToast
import com.jooheon.toyplayer.features.common.utils.MusicUtil
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.ext.uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlaybackLogUseCase(
    private val context: Context,
    private val musicStateHolder: MusicStateHolder,
    private val networkConnectivityObserver: NetworkConnectivityObserver,
) {
    private var startTimeMs: Long = C.TIME_UNSET
    private var targetSong = Song.default

    internal fun initialize(scope: CoroutineScope) {
        collectPlaybackLogStates(scope)
    }

    private fun collectPlaybackLogStates(scope: CoroutineScope) {
        scope.launch {
            launch {
                musicStateHolder.disContinuation.collectLatest { (oldPosition, newPosition) ->
                    startTimeMs = newPosition

                    val old = MusicUtil.toReadableDurationString(oldPosition)
                    val new = MusicUtil.toReadableDurationString(newPosition)
                    Timber.d("collectDiscontinuation: [$old, $new]")
                }
            }

            launch {
                musicStateHolder.mediaItem.collectLatest { mediaItem ->
                    if(mediaItem == null ||  mediaItem.mediaId.toLongOrNull() == 0L) {
                        Timber.i( "collectMediaItem: mediaItem is invalid")

                        startTimeMs = C.TIME_UNSET
                        targetSong = Song.default
                        return@collectLatest
                    }
                    Timber.d( "collectMediaItem: [${mediaItem.mediaMetadata.title}]")

                    /** 곡이 바뀌었을 때 **/
                    enqueuePlaybackLog(
                        song = targetSong,
                        startTimeMs = startTimeMs,
                        duration = musicStateHolder.currentDuration.value
                    )

                    targetSong = mediaItem.toSong()
                }
            }
        }
    }

    private suspend fun enqueuePlaybackLog(
        song: Song,
        startTimeMs: Long,
        duration: Long,
    ) = withContext(Dispatchers.Main) {
        if(song == Song.default || startTimeMs == C.TIME_UNSET) {
            Timber.i("variable is initial value: [${song.key()}, $startTimeMs]")
            return@withContext
        }

        val startTime = MusicUtil.toReadableDurationString(startTimeMs)
        val endTime = MusicUtil.toReadableDurationString(duration)

        val listenedDuration = duration - startTimeMs
        if(listenedDuration < 1000L) {
            Timber.e("listenedDuration is too small [$startTime, $endTime] ($listenedDuration)")
            return@withContext
        }

        val networkAvailable = networkConnectivityObserver.networkAvailable()

        Timber.d( "enqueueChargeLog: ${song.title}, [$startTime ~ $endTime], ${song.uri}")
        val title = song.title.defaultEmpty()
        val size = if(title.length < 16) title.length else 16
        val shortTitle = title.substring(0, size)

        context.showToast("$shortTitle, $duration [$startTime ~ $endTime]")
    }
}