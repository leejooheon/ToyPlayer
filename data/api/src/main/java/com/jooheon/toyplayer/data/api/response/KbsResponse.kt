package com.jooheon.toyplayer.data.api.response

import com.jooheon.toyplayer.domain.model.RadioRawData
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

    fun toSongOrNull(index: Int, rawData: RadioRawData): Song? {
        val channelItem = parseChannelItem() ?: return null
        val kbsId = "KBS".hashCode().toString()
        return Song(
            audioId = rawData.channelName.hashCode().toLong(),
            useCache = false,
            displayName = rawData.channelName,
            title = rawData.channelName,
            artist = channelMaster.ppsKindLabel.defaultEmpty(),
            artistId = kbsId,
            album = channelMaster.title.defaultEmpty(),
            albumId = kbsId,
            duration = -1,
            path = channelItem.serviceUrl,
            imageUrl = channelMaster.imagePathChannelLogo.defaultEmpty(),
            trackNumber = index
        )
    }

    private fun parseChannelItem(): ChannelItem? {
        val item = channelItems
            .firstOrNull { it.mediaType == "radio" }
            ?.let { channelItems.firstOrNull() }

        return item
    }
}