package com.jooheon.toyplayer.data.equalizer.dao.data

import androidx.room.TypeConverter

class PresetListConverter {
    @TypeConverter
    fun fromFloatList(list: List<Float>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toFloatList(data: String): List<Float> {
        return if (data.isBlank()) emptyList()
        else data.split(",").map { it.toFloatOrNull() ?: 0f }
    }
}