package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.common.Result

interface MusicListRepository {
    suspend fun getMusicFromAsset(): Result<List<Song>, PlaybackDataError>
    suspend fun getLocalMusicList(): Result<List<Song>, PlaybackDataError>
    suspend fun getStreamingMusicList(): Result<List<Song>, PlaybackDataError>
}