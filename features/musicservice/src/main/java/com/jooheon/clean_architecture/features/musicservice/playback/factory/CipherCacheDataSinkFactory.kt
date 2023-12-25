package com.jooheon.clean_architecture.features.musicservice.playback.factory

import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.AesCipherDataSink
import androidx.media3.datasource.DataSink
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSink
import com.jooheon.clean_architecture.features.musicservice.playback.PlaybackCacheManager.Companion.SECRET_KEY

class CipherCacheDataSinkFactory(private val cache: Cache): DataSink.Factory {
    @UnstableApi override fun createDataSink(): DataSink {
        val cacheSink = CacheDataSink(
            cache,
            Long.MAX_VALUE,
            CacheDataSink.DEFAULT_BUFFER_SIZE * 2
        )
        val scratch = ByteArray(3897)
        return AesCipherDataSink(
            Util.getUtf8Bytes(SECRET_KEY),
            cacheSink,
            scratch
        )
    }
}