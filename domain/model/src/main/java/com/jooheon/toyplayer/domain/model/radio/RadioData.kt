package com.jooheon.toyplayer.domain.model.radio

import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

@Serializable
data class RadioData(
    val type: RadioType,
    val url: String?,
    val channelName: String,
    val channelCode: String,
    val channelSubCode: String? = null,
    val updatedTime: Long? = null,
) {
    fun serialize() = Json.encodeToString(serializer(), this)
    fun mediaId() = "${type.name()}_${channelCode}_${channelSubCode}"

    fun toSong(index: Int): Song = Song(
        audioId = mediaId().hashCode().toLong(),
        useCache = false,
        displayName = channelName,
        title = channelName,
        artist = type.name(),
        artistId = type.name(),
        album = "Live Radio",
        albumId = "Radio",
        duration = -1,
        path = url.defaultEmpty(),
        trackNumber = index,
        imageUrl = "",
        isFavorite = false,
        data = serialize()
    )

    companion object {
        const val BUNDLE_TYPE = "bundle_type"
        const val BUNDLE_URL = "bundle_url"
        const val BUNDLE_CHANNEL_NAME = "bundle_channel_name"
        const val BUNDLE_CHANNEL_CODE = "bundle_channel_code"
        const val BUNDLE_CHANNEL_SUB_CODE = "bundle_channel_sub_code"

        fun String.toRadioDataOrNull(): RadioData? {
            return try {
                Json.decodeFromString(serializer(), this@toRadioDataOrNull)
            } catch (e: SerializationException) {
                null
            }
        }
    }
}