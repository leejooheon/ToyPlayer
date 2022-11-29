package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.music.MusicUseCase
import kotlinx.coroutines.flow.Flow

class EmptyMusicUseCase: MusicUseCase {
    override fun getAlbums(uri: String): Flow<Resource<List<Entity.Song>>> {
        TODO("Not yet implemented")
    }

    override fun getSongs(uri: String): Flow<Resource<List<Entity.Song>>> {
        TODO("Not yet implemented")
    }

    override fun getSongsSync(uri: String): Flow<Resource<List<Entity.Song>>> {
        TODO("Not yet implemented")
    }

    companion object {
        fun dummyData(): List<Entity.Song> {
            val songs = mutableListOf<Entity.Song>()
            for (i in 1..3) {
                val song = Entity.Song(
                    id = i.toLong(),
                    title = "title - $i",
                    trackNumber = i * 5,
                    year = i * 1000,
                    duration = (i * 500).toLong(),
                    data = "data - $i",
                    dateModified = (i * 500).toLong(),
                    albumId = (i * 600).toLong(),
                    albumName = "album_name $i",
                    artistId = (i * 700).toLong(),
                    artistName = "artist_name $i",
                    composer = null,
                    albumArtist = null
                )
                songs.add(song)
            }
            return songs
        }
    }
}