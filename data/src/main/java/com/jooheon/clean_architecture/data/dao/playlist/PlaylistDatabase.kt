package com.jooheon.clean_architecture.data.dao.playlist

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jooheon.clean_architecture.data.dao.playlist.data.PlaylistEntity
import com.jooheon.clean_architecture.data.dao.playlist.data.SongListConverter

@Database(
    entities = [PlaylistEntity::class],
    version = 1
)
@TypeConverters(SongListConverter::class)
abstract class PlaylistDatabase: RoomDatabase() {
    abstract val dao: PlaylistDao
}