package com.jooheon.toyplayer.data.playlist.dao.data

import androidx.room.TypeConverter
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SongListConverter {
    @TypeConverter
    fun listToJson(data: List<Song>?): String? {
        data ?: return null
        return Json.encodeToString(data)
    }

    @TypeConverter
    fun jsonToList(data: String): List<Song>? {
        return try {
            Json.decodeFromString<List<Song>>(data)
        } catch (e: Exception) {
            null
        }
    }
}