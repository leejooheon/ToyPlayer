package com.jooheon.toyplayer.data.dao.playlist.data

import androidx.room.TypeConverter
import com.jooheon.toyplayer.domain.entity.music.Song
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
            val songs = Json.decodeFromString<List<Song>>(data)
            songs
        } catch (e: Exception) {
            null
        }
    }
}