package com.jooheon.toyplayer.data.api.response

import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KbsResponse(
    @SerialName("channel_item")
    val channelItems : List<ChannelItem>,
    @SerialName("channelMaster")
    val channelMaster: ChannelMaster,
) {
    @Serializable
    data class ChannelItem(
        @SerialName("service_url") val serviceUrl: String,
        @SerialName("media_type") val mediaType: String
    )

    @Serializable
    data class ChannelMaster(
        @SerialName("pps_kind_label") val ppsKindLabel: String?,
        @SerialName("image_path_channel_logo") val imagePathChannelLogo: String?,
        @SerialName("title") val title: String?
    )

    fun parseRadioUrl(): String? {
        val item = channelItems
            .firstOrNull { it.mediaType == "radio" }
            ?.let { channelItems.firstOrNull() }

        return item?.serviceUrl
    }
}