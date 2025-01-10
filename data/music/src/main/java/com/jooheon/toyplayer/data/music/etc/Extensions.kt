package com.jooheon.toyplayer.data.music.etc

import android.database.Cursor
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Song
import org.json.JSONException
import org.json.JSONObject

internal fun JSONObject.toSong(): Song? {
    try {
        val trackId = this.getString("id")
        val albumName = this.getString("album")
        val title = this.getString("title")
        val artistName = this.getString("artist")
        val trackNumber = this.getInt("trackNumber")
        val duration = (this.getInt("duration") * 1000).toLong()
        val sourceUri = this.getString("source")
        val imageUri = this.getString("image")

        return Song(
            audioId = trackId.hashCode().toLong(),
            useCache = true,
            displayName = trackId,
            title = title.defaultEmpty(),
            artist = artistName.defaultEmpty(),
            artistId = "unset",
            album = albumName.defaultEmpty(),
            albumId = "unset",
            duration = duration,
            path = sourceUri,
            trackNumber = trackNumber % 1000,
            imageUrl = imageUri,
            isFavorite = false,
        )
    } catch (e: JSONException) {
        return null
    }
}

internal fun Cursor.getStringOrNull(columnName: String): String? {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}
internal fun Cursor.getLong(columnName: String): Long {
    try {
        return getLong(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}
internal fun Cursor.getInt(columnName: String): Int {
    try {
        return getInt(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}