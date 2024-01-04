package com.jooheon.toyplayer.data.dao.playlist.data

import com.jooheon.toyplayer.domain.common.Mapper
import com.jooheon.toyplayer.domain.entity.music.Playlist

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