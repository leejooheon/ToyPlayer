package com.jooheon.toyplayer.domain.usecase

import com.jooheon.toyplayer.domain.common.Result
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.MusicListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MusicListUseCase @Inject constructor(
    private val musicListRepository: MusicListRepository,
) {
    suspend fun getAllSongList(): List<Song> {
        val local = getLocalSongList()
        val stream = getStreamingUrlList()
        val asset = getSongListFromAsset()
        val allSongs = local + stream + asset

        return allSongs
    }

    suspend fun getLocalSongList(): List<Song> {
        val result = musicListRepository.getLocalMusicList()

        return when(result) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }

    suspend fun getStreamingUrlList(): List<Song> {
        val result = musicListRepository.getStreamingMusicList()

        return when(result) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }

    suspend fun getSongListFromAsset(): List<Song> {
        val result = musicListRepository.getMusicFromAsset()

        return when(result) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }
}