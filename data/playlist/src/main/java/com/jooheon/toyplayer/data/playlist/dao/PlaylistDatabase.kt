package com.jooheon.toyplayer.data.playlist.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jooheon.toyplayer.data.dao.playlist.data.PlaylistEntity
import com.jooheon.toyplayer.data.playlist.dao.data.SongListConverter

@Database(
    entities = [PlaylistEntity::class],
    version = 1
)
@TypeConverters(SongListConverter::class)
abstract class PlaylistDatabase: RoomDatabase() {
    abstract val dao: PlaylistDao
}