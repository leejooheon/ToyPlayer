package com.jooheon.toyplayer.features.musicservice.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import timber.log.Timber

@OptIn(UnstableApi::class)
class HlsPlaybackUriResolver(

) : ResolvingDataSource.Resolver {
    override fun resolveDataSpec(dataSpec: DataSpec): DataSpec {
        Timber.d("resolveDataSpec: ${dataSpec.uri}")

        val uri = dataSpec.uri

        val expiresIn = uri.getQueryParameter("Expires")?.toLongOrNull()

        if(expiresIn != null) {
            // update dataSpec
        }

        return dataSpec
    }
}