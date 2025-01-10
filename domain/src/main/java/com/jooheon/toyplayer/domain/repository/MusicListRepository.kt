package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.common.Result
import com.jooheon.toyplayer.domain.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song

interface MusicListRepository {
    suspend fun getMusicFromAsset(): Result<List<Song>, MusicDataError>
    suspend fun getLocalMusicList(): Result<List<Song>, MusicDataError>
    suspend fun getStreamingMusicList(): Result<List<Song>, MusicDataError>
}