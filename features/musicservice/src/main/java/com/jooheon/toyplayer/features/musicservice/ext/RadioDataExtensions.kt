@file:UnstableApi
package com.jooheon.toyplayer.features.musicservice.ext

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.BUNDLE_CHANNEL_CODE
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.BUNDLE_CHANNEL_NAME
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.BUNDLE_CHANNEL_SUB_CODE
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.BUNDLE_TYPE
import com.jooheon.toyplayer.domain.model.radio.RadioData.Companion.BUNDLE_URL
import com.jooheon.toyplayer.domain.model.radio.RadioType
import com.jooheon.toyplayer.domain.model.radio.RadioType.Companion.toRadioTypeOrNull
import timber.log.Timber

fun MediaItem.toRadioDataOrNull(): RadioData? {
    fun getType(extras: Bundle?): RadioType? = extras?.getString(BUNDLE_TYPE).defaultEmpty().toRadioTypeOrNull()
    fun getUrl(extras: Bundle?): String = extras?.getString(BUNDLE_URL).defaultEmpty()
    fun getChannelName(extras: Bundle?): String = extras?.getString(BUNDLE_CHANNEL_NAME).defaultEmpty()
    fun getChannelCode(extras: Bundle?): String = extras?.getString(BUNDLE_CHANNEL_CODE).defaultEmpty()
    fun getChannelSubCode(extras: Bundle?): String? = extras?.getString(BUNDLE_CHANNEL_SUB_CODE)

    return with(mediaMetadata) {
        val type = getType(extras) ?: return null
        RadioData(
            type = type,
            url = getUrl(extras),
            channelName = getChannelName(extras),
            channelCode = getChannelCode(extras),
            channelSubCode = getChannelSubCode(extras),
        )
    }
}

fun RadioData.extras() = bundleOf(
    BUNDLE_TYPE to type.serialize(),
    BUNDLE_CHANNEL_NAME to channelName,
    BUNDLE_CHANNEL_CODE to channelCode,
    BUNDLE_CHANNEL_SUB_CODE to channelSubCode,
)