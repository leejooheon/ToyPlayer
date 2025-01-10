package com.jooheon.toyplayer.data.playlist.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jooheon.toyplayer.data.dao.playlist.data.PlaylistEntity

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylist(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE id LIKE :mID")
    fun get(mID: Int): PlaylistEntity?

    @Update
    suspend fun update(vararg playlist: PlaylistEntity)

    @Delete
    suspend fun delete(vararg playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg playlist: PlaylistEntity)
}