package com.jooheon.toyplayer.features.musicservice.playback.factory

import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.AesCipherDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.FileDataSource
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager.Companion.SECRET_KEY

class CipherCacheDataSourceFactory : DataSource.Factory {
    @UnstableApi override fun createDataSource(): DataSource {
        val fileDataSource = FileDataSource()
        return AesCipherDataSource(Util.getUtf8Bytes(SECRET_KEY), fileDataSource)
    }
}