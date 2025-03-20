package com.jooheon.toyplayer.data.api.response

import android.content.Context
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.serialization.Serializable

@Serializable
data class StreamResponse(
    val audioId: Long,
    val title: String,
    val url: String,
    val image: String
) {
    fun toSong(context: Context): Song {
        val imageData = image.split("/")
        val resourceId = context.resources.getIdentifier(imageData.last(), imageData.first(), context.packageName)
        val imageUrl = "android.resource://${context.packageName}/$resourceId"

        return Song.default.copy(
            audioId = audioId,
            useCache = false,
            displayName = title,
            title = title,
            artist = "streaming",
            album = "streaming",
            path = url,
            imageUrl = imageUrl,
        )
    }
}