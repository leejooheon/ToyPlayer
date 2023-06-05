package com.jooheon.clean_architecture.data.dao.playlist.data

import com.jooheon.clean_architecture.domain.common.Mapper
import com.jooheon.clean_architecture.domain.entity.music.Playlist

class PlaylistMapper: Mapper<PlaylistEntity, Playlist>() {
    override fun map(data: PlaylistEntity): Playlist {
        return Playlist(
            id = data.id,
            name = data.name,
            thumbnailUrl = data.thumbnailUrl,
            songs = data.songs
        )
    }
    override fun mapInverse(data: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = data.id,
            name = data.name,
            thumbnailUrl = data.thumbnailUrl,
            songs = data.songs
        )
    }
}