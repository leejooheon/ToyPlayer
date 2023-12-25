package com.jooheon.clean_architecture.features.musicservice.playback

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import com.jooheon.clean_architecture.features.musicservice.playback.factory.CipherCacheDataSinkFactory
import com.jooheon.clean_architecture.features.musicservice.playback.factory.CipherCacheDataSourceFactory
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
//        cache = SimpleCache(
//            /** file **/ cacheDirectory(context),
//            /** CacheEvictor **/ NoOpCacheEvictor(),
//            /** databaseProvider **/ StandaloneDatabaseProvider(context),
//            /** legacyIndexSecretKey **/ SECRET_KEY.toByteArray(),
//            /** legacyIndexEncrypt **/ true,
//            /** preferLegacyIndex **/ true,
//        )
    }

    internal fun release() {
        cache.release()
    }

    internal fun isCached(key: String, position: Long, length: Long): Boolean {
        val cached = cache.isCached(key, position, length)
        Timber.tag(CACHE_TAG).d("isCached: ${cached}, [key: $key, position: $position, length: $length]")
        return cached
    }

    internal fun cacheDataSource(): DataSource.Factory {
        /**
         * https://github.com/google/ExoPlayer/issues/7887
         * https://github.com/google/ExoPlayer/issues/5083
         * https://github.com/google/ExoPlayer/issues/6472
         * https://github.com/google/ExoPlayer/issues/5193
         * https://github.com/google/ExoPlayer/issues/7566
         * https://medium.com/@eguven/you-can-use-aescipherdatasink-to-encrypt-the-downloaded-files-and-aescipherdatasource-to-read-them-a3ce4434e1dd
         */

        return CacheDataSource.Factory()
            .setCache(cache)
            .setCacheReadDataSourceFactory(CipherCacheDataSourceFactory())
            .setCacheWriteDataSinkFactory(CipherCacheDataSinkFactory(cache))
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context)) // 캐시에 없는 미디어를 재생할때 사용
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
        internal const val CACHE_TAG = "Cache@Main"
        const val SECRET_KEY = "AES_KEY_JOO_HEON"
        const val SECRET_IV = "AES256_SECRET_IV"
    }
}
