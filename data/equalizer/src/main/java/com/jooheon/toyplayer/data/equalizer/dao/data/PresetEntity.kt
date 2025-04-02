package com.jooheon.toyplayer.data.equalizer.dao.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset

@Entity(tableName = "preset_table")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "gains") val gains : List<Float>,
    @ColumnInfo(name = "type") val type: EqualizerType
) {
    internal fun toPreset() = Preset(
        id = id,
        name = name,
        gains = gains,
        type = type,
        isCustom = true,
    )

    companion object {
        internal fun Preset.toPresetEntity() = PresetEntity(
            id = id,
            name = name,
            gains = gains,
            type = type
        )
    }
}