package com.jooheon.toyplayer.features.musicservice.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.data.TestLogKey
import com.jooheon.toyplayer.features.musicservice.ext.toSong
import com.jooheon.toyplayer.features.musicservice.ext.uri
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager.Companion.chunkLength
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.random.Random

@OptIn(UnstableApi::class)
class PlaybackUriResolver(
    private val musicStateHolder: MusicStateHolder,
    private val playbackCacheManager: PlaybackCacheManager,
) : ResolvingDataSource.Resolver {
    private val TAG = PlaybackUriResolver::class.java.simpleName

    private val random = Random(System.currentTimeMillis())

    override fun resolveDataSpec(dataSpec: DataSpec): DataSpec {
        val customCacheKey = dataSpec.key ?: run {
            Timber.tag(TAG).e("dataSpec key is null")
            error("A key must be set")
        }
        val mediaItems = runBlocking { musicStateHolder.mediaItems.firstOrNull().defaultEmpty() }
        val mediaItem = mediaItems.firstOrNull { it.mediaId == customCacheKey } ?: run {
            Timber.tag(TAG).d("resolveDataSpec: $customCacheKey is not found in playlist.")
            throw PlaybackException("$customCacheKey not found in playlist." , null, PlaybackException.ERROR_CODE_FAILED_RUNTIME_CHECK)
        }
        val song = mediaItem.toSong()

        val uri = runBlocking(Dispatchers.IO) {
            val streamResult = musicStateHolder.getStreamResultOrNull(song.key())
            streamResult ?: getStreamUri(song)
        } ?: run {
            throw PlaybackException("StreamResult is null. [${song.title}]" , null, PlaybackException.CUSTOM_ERROR_CODE_BASE)
        }

        isCached(
            dataSpec = dataSpec,
            song = song,
        ).also { cached ->
            Timber.tag(TAG).d("resolveDataSpec: isCached: $cached")
            if(cached) return dataSpec.withCustomData(song.key())
        }

        val newDataSpec = dataSpec
            .withUri(uri)
            .withCustomData(song.key())

        if(song.useCache) {
            newDataSpec.subrange(dataSpec.uriPositionOffset, chunkLength)
        }

        Timber.tag(TAG).i("resolveDataSpec: customData: ${newDataSpec.customData} key: ${newDataSpec.key}")

        return newDataSpec
    }

    override fun resolveReportedUri(uri: Uri): Uri {
        Timber.tag(TAG).i("resolveReportedUri: $uri")
        return super.resolveReportedUri(uri)
    }

    private fun isCached(dataSpec: DataSpec, song: Song): Boolean {
        if(song.useCache) {
            val cached = playbackCacheManager?.isCached(song.key(), dataSpec.position, chunkLength).defaultFalse()
            Timber.tag(PlaybackCacheManager.CACHE_TAG).d("isCached: ${cached}, [${song.title} key: ${song.key()}, position: ${dataSpec.position}, length: $chunkLength]")
            return cached
        }

        return false
    }

    private suspend fun getStreamUri(song: Song): Uri? {
        val overwriteSession = true

        val (musicStreamUri, logKey) = runBlocking(Dispatchers.IO) {
            getMusicStream(song, overwriteSession)
        } ?: run {
            Timber.tag(TAG).e("musicStreamUri is null")
            return null
        }

        if(musicStreamUri == null || musicStreamUri.toString().isEmpty()) {
            Timber.tag(TAG).e("musicStreamUri is invalid: $musicStreamUri")
            return null
        }

        if(logKey == null) {
            Timber.tag(TAG).d("streamResult is null: ${song.uri}")
            return null
        }

        Timber.tag(TAG).d("getMusicStream success. title: ${song.title}, id: ${song.key()}, overwriteSession: $overwriteSession, logKey: $logKey")
        musicStateHolder.appendToRingBuffer(song.key() to song.uri)
        return musicStreamUri
    }

    private suspend fun getMusicStream(
        song: Song,
        overwriteSession: Boolean
    ): Pair<Uri?, String?>? {
        val logKey = withContext(Dispatchers.IO) {
            delay(random.nextLong(500))
            TestLogKey.random()
        }
        return Pair(song.uri, logKey)
    }

    private fun DataSpec.withCustomData(customData: Any): DataSpec {
        return DataSpec.Builder().setUri(uri)
            .setUriPositionOffset(uriPositionOffset)
            .setHttpMethod(httpMethod)
            .setHttpBody(httpBody)
            .setHttpRequestHeaders(httpRequestHeaders)
            .setPosition(position)
            .setLength(length)
            .setKey(key)
            .setFlags(flags)
            .setCustomData(customData)
            .build()
    }
}
