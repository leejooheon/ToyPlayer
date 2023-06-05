package com.jooheon.clean_architecture.data.dao.playlist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jooheon.clean_architecture.domain.entity.music.Song

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnailUrl: String,
    @ColumnInfo(name = "songs") val songs: List<Song>,
)