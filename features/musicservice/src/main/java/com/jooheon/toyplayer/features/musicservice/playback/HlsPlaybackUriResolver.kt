package com.jooheon.toyplayer.features.musicservice.playback

import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.toRadioDataOrNull
import com.jooheon.toyplayer.domain.usecase.RadioUseCase
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@OptIn(UnstableApi::class)
class HlsPlaybackUriResolver(
    private val radioUseCase: RadioUseCase
) : ResolvingDataSource.Resolver {
    override fun resolveDataSpec(dataSpec: DataSpec): DataSpec {
        Timber.d("resolveDataSpec: ${dataSpec.uri}, ${dataSpec.customData}")

        dataSpec.uri.toString().toRadioDataOrNull()?.let {
            Timber.d("trying to parse from uri")
            return updateDataSpec(it, dataSpec)
        }

        dataSpec.customData.toString().toRadioDataOrNull()?.let {
            Timber.d("trying to parse from customData")
            if(isUpdateNeeded(it)) {
                return updateDataSpec(it, dataSpec)
            }
        }

        Timber.d("returning original DataSpec")
        return dataSpec
    }

    private fun updateDataSpec(radioData: RadioData, dataSpec: DataSpec): DataSpec {
        val uri = getStreamUrl(radioData)
        val updatedRadioData = radioData.copy(updatedTime = System.currentTimeMillis())
        Timber.d("returning new DataSpec - $updatedRadioData")

        return dataSpec.buildUpon()
            .setUri(uri)
            .setCustomData(updatedRadioData.serialize())
            .build()
    }

    private fun getStreamUrl(radioData: RadioData): Uri {
        val uri = runBlocking {
            val result = radioUseCase.getRadioUrl(radioData)
            when(result) {
                is Result.Success -> result.data.toUri()
                is Result.Error -> throw IllegalStateException("${result.error}")
            }
        }
        return uri
    }

    private fun isUpdateNeeded(radioData: RadioData): Boolean {
        // Check if updatedTime is older than 24 hours
        val isExpired = System.currentTimeMillis() > radioData.updatedTime.defaultZero() + 360000 * 24
        Timber.d("updatedTime - ${radioData.updatedTime}, isExpired - $isExpired")
        return isExpired
    }
}