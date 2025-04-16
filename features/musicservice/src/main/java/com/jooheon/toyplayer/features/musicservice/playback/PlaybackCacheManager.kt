package com.jooheon.toyplayer.features.musicservice.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.extractor.DefaultExtractorsFactory
import com.jooheon.toyplayer.features.musicservice.playback.factory.CipherCacheDataSinkFactory
import com.jooheon.toyplayer.features.musicservice.playback.factory.CipherCacheDataSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(UnstableApi::class)
class PlaybackCacheManager(private val context: Context) {
    private val cache = SimpleCache(
        cacheDirectory(context),
        LeastRecentlyUsedCacheEvictor(DiskCacheMaxSize.`1GB`.bytes), // TODO: 설정에서 사용자에게 입력받자.
        StandaloneDatabaseProvider(context)
    )

    internal fun release() {
        cache.release()
    }

    fun isCached(key: String, position: Long, length: Long): Boolean {
        return cache.isCached(key, position, length)
    }

    internal fun cacheDataSource(): DataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setCacheReadDataSourceFactory(CipherCacheDataSourceFactory())
            .setCacheWriteDataSinkFactory(CipherCacheDataSinkFactory(cache))
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context, )) // 캐시에 없는 미디어를 재생할때 사용
            .setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private fun cacheDirectory(context: Context): File {
        val cacheDirectory = File(context.externalCacheDir, "/playback").also { directory ->
            if (directory.exists()) return@also
            directory.mkdir()
        }

        return cacheDirectory
    }

    companion object {
        const val SECRET_KEY = "AES_KEY_JOO_HEON"
    }
}
