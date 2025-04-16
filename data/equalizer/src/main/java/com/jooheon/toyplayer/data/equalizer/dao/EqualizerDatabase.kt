package com.jooheon.toyplayer.data.equalizer.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jooheon.toyplayer.data.equalizer.dao.data.PresetEntity
import com.jooheon.toyplayer.data.equalizer.dao.data.PresetListConverter

@Database(
    entities = [PresetEntity::class],
    version = 1
)
@TypeConverters(PresetListConverter::class)
abstract class EqualizerDatabase: RoomDatabase() {
    abstract val dao: EqualizerDao
}