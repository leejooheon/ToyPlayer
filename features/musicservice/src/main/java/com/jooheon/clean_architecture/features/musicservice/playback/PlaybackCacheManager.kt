package com.jooheon.clean_architecture.features.musicservice.playback

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@UnstableApi
class PlaybackCacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private lateinit var cache: SimpleCache

    internal fun init() {
        cache = SimpleCache(
            cacheDirectory(context),
            NoOpCacheEvictor(),
            StandaloneDatabaseProvider(context)
        )
    }

    internal fun release() {
        cache.release()
    }

    internal fun isCached(key: String, position: Long, length: Long): Boolean {
        val cached = cache.isCached(key, position, length)
        Timber.d("isCached: ${cached}, [key: $key, position: $position, length: $length]")
        return cached
    }

    internal fun cacheDataSource(): DataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
//            .setCacheWriteDataSinkFactory(BugsCacheWriteDataSink.Factory(context, cache))
//            .setCacheReadDataSourceFactory(BugsCacheReadDataSource.Factory(context))
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context)) // 캐시에 없는 미디어를 재생할때 사용
    }

    private fun cacheDirectory(context: Context): File {
        val cacheDirectory = File(context.externalCacheDir, "/playback/cache").also { directory ->
            if (directory.exists()) return@also
            directory.mkdir()
        }

        return cacheDirectory
    }
}
