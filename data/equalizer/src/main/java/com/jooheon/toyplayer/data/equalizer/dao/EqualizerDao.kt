package com.jooheon.toyplayer.data.equalizer.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jooheon.toyplayer.data.equalizer.dao.data.PresetEntity
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import kotlinx.coroutines.flow.Flow

@Dao
interface EqualizerDao {
    @Query("SELECT * FROM preset_table")
    fun getAllPresets(): Flow<List<PresetEntity>>

    @Query("SELECT * FROM preset_table WHERE type = :type")
    fun getPresets(type: EqualizerType): Flow<List<PresetEntity>>

    @Query("SELECT * FROM preset_table WHERE id LIKE :mID")
    fun get(mID: Int): Flow<PresetEntity?>

    @Query("SELECT COUNT(*) FROM preset_table")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM preset_table WHERE type = :type")
    suspend fun countByType(type: EqualizerType): Int

    @Update
    suspend fun update(vararg playlist: PresetEntity)

    @Delete
    suspend fun delete(vararg playlist: PresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg playlist: PresetEntity)
}