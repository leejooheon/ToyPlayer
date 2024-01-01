package com.jooheon.clean_architecture.features.musicservice.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import com.jooheon.clean_architecture.domain.common.extension.defaultFalse
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.clean_architecture.domain.usecase.music.library.PlayingQueueUseCase
import com.jooheon.clean_architecture.features.musicservice.data.RingBuffer
import com.jooheon.clean_architecture.features.musicservice.data.TestLogKey
import com.jooheon.clean_architecture.features.musicservice.ext.uri
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackCacheManager.Companion.chunkLength
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.random.Random

@OptIn(UnstableApi::class)
class PlaybackUriResolver(
    private val playingQueueUseCase: PlayingQueueUseCase,
    private val automotiveUseCase: AutomotiveUseCase,
) : ResolvingDataSource.Resolver {
    private val TAG = PlaybackUriResolver::class.java.simpleName

    private var playbackCacheManager: PlaybackCacheManager? = null

    private val ringBuffer = RingBuffer<Pair<String, Uri>?>(URI_BUFFER_SIZE) { null }
    private val random = Random(System.currentTimeMillis())

    fun init(playbackCacheManager: PlaybackCacheManager) {
        this.playbackCacheManager = playbackCacheManager
    }
    fun release() {
        playbackCacheManager = null
    }

    override fun resolveDataSpec(dataSpec: DataSpec): DataSpec {
        val customCacheKey = dataSpec.key ?: run {
            Timber.tag(TAG).e("dataSpec key is null")
            error("A key must be set")
        }

        val (playingQueue, automotiveQueue) = runBlocking {
            Pair(playingQueueUseCase.getPlayingQueue(), automotiveUseCase.getCurrentPlayingSongs())
        }

        val song = findSong(customCacheKey, playingQueue + automotiveQueue)

        isCached(
            dataSpec = dataSpec,
            song = song,
        ).also { cached ->
            Timber.tag(TAG).d("resolveDataSpec: isCached: $cached")
            if(cached) return dataSpec.withCustomData(song.key())
        }

        repeat(URI_BUFFER_SIZE) { index ->
            val (key, uri) = ringBuffer.getOrNull(index) ?: return@repeat
            if(customCacheKey == key) {
                Timber.tag(TAG).d("resolveDataSpec: inside ringBuffer: $customCacheKey, $uri")
                return dataSpec.withUri(uri).withCustomData(song.key())
            }
        }

        val uri = getStreamUri(song)
        ringBuffer.append(customCacheKey to uri)

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

    private fun findSong(customCacheKey: String, playingQueue: List<Song>): Song {
        val song = playingQueue.firstOrNull { it.key() == customCacheKey } ?: run {
            Timber.tag(TAG).d("resolveDataSpec: $customCacheKey is not found in playlist.")
            throw PlaybackException("key not found in playlist.", null, PlaybackException.ERROR_CODE_REMOTE_ERROR)
        }
        return song
    }

    private fun isCached(dataSpec: DataSpec, song: Song): Boolean {
        if(song.useCache) {
            val cached = playbackCacheManager?.isCached(song.key(), dataSpec.position, chunkLength).defaultFalse()
            Timber.tag(PlaybackCacheManager.CACHE_TAG).d("isCached: ${cached}, [${song.title} key: ${song.key()}, position: ${dataSpec.position}, length: $chunkLength]")
            return cached
        }

        return false
    }

    private fun getStreamUri(song: Song): Uri {
        val defaultUri = song.uri
        val overwriteSession = true

        val (musicStreamUri, logKey) = runBlocking(Dispatchers.IO) {
            getMusicStream(song, overwriteSession)
        } ?: run {
            Timber.tag(TAG).e("musicStreamUri is null")
            return defaultUri
        }

        if(musicStreamUri == null || musicStreamUri.toString().isEmpty()) {
            Timber.tag(TAG).e("musicStreamUri is invalid: $musicStreamUri")
            return defaultUri
        }

        if(logKey == null) {
            Timber.tag(TAG).d("streamResult is null: ${song.uri}")
            return defaultUri
        }

        Timber.tag(TAG).d("getMusicStream success. title: ${song.title}, id: ${song.key()}, overwriteSession: $overwriteSession, logKey: $logKey")

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

    companion object {
        private const val URI_BUFFER_SIZE = 2
    }
}
