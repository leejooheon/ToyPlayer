package com.jooheon.toyplayer.features.musicservice.playback.factory

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.AesCipherDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.FileDataSource
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager.Companion.SECRET_KEY

@OptIn(UnstableApi::class)
class CipherCacheDataSourceFactory : DataSource.Factory {
    override fun createDataSource(): DataSource {
        val fileDataSource = FileDataSource()
        return AesCipherDataSource(Util.getUtf8Bytes(SECRET_KEY), fileDataSource)
    }
}