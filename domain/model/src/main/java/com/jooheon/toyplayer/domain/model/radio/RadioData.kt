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
    val imageUrl: String,
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
        imageUrl = "imageUrl",
        isFavorite = false,
        data = serialize()
    )

    companion object {
        fun String.toRadioDataOrNull(): RadioData? {
            return try {
                Json.decodeFromString(serializer(), this@toRadioDataOrNull)
            } catch (e: SerializationException) {
                null
            }
        }

        val default = RadioData(
            type = RadioType.Etc("default"),
            url = null,
            imageUrl = "",
            channelName = "default",
            channelCode = "default",
        )
    }
}