package com.jooheon.toyplayer.data.playlist.dao.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail") val thumbnailUrl: String,
    @ColumnInfo(name = "songs") val songs: List<Song>,
) {
    internal fun toPlaylist() = Playlist(
        id = id,
        name = name,
        thumbnailUrl = thumbnailUrl,
        songs = songs
    )

    companion object {
        internal fun Playlist.toPlaylistEntity() = PlaylistEntity(
            id = id,
            name = name,
            thumbnailUrl = thumbnailUrl,
            songs = songs
        )
    }
}