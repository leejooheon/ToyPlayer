package com.jooheon.clean_architecture.presentation.view.temp

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.music.MusicPlayListUsecase

class EmptyMusicPlayListUsecase: MusicPlayListUsecase {
    override suspend fun getLocalSongList(uri: String): Resource<MutableList<Song>> {
        TODO("Not yet implemented")
    }

    override suspend fun getStreamingUrlList(): Resource<MutableList<Song>> {
        TODO("Not yet implemented")
    }

    companion object {
        fun dummyData(): List<Song> {
            val songs = mutableListOf<Song>()
            for (i in 1..3) {
                val song = Song.default.copy(
                    title = "dummy_${i}"
                )
                songs.add(song)
            }
            return songs
        }
    }
}