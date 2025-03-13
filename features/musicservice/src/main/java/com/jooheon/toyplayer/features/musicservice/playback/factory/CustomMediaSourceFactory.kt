package com.jooheon.toyplayer.features.musicservice.playback.factory

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.drm.DrmSessionManagerProvider
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.upstream.LoadErrorHandlingPolicy
import timber.log.Timber

@OptIn(UnstableApi::class)
class CustomMediaSourceFactory(
    defaultDataSource: DataSource.Factory,
    hlsDataSource: DataSource.Factory
): MediaSource.Factory {
    private val defaultMediaSourceFactory = DefaultMediaSourceFactory(defaultDataSource)
    private val hlsMediaSourceFactory = HlsMediaSource.Factory(hlsDataSource)

    override fun createMediaSource(mediaItem: MediaItem): MediaSource {
        val mimeType = mediaItem.localConfiguration?.mimeType
        Timber.d("createMediaSource: ${mediaItem.mediaMetadata.title}, $mimeType")

        return when(mediaItem.localConfiguration?.mimeType) {
            MimeTypes.APPLICATION_M3U8 -> hlsMediaSourceFactory.createMediaSource(mediaItem)
            else -> defaultMediaSourceFactory.createMediaSource(mediaItem)
        }
    }

    override fun getSupportedTypes(): IntArray {
        return defaultMediaSourceFactory.supportedTypes + hlsMediaSourceFactory.supportedTypes
    }

    override fun setDrmSessionManagerProvider(drmSessionManagerProvider: DrmSessionManagerProvider): MediaSource.Factory {
        return defaultMediaSourceFactory.setDrmSessionManagerProvider(drmSessionManagerProvider)
    }

    override fun setLoadErrorHandlingPolicy(loadErrorHandlingPolicy: LoadErrorHandlingPolicy): MediaSource.Factory {
        return defaultMediaSourceFactory.setLoadErrorHandlingPolicy(loadErrorHandlingPolicy)
    }
}