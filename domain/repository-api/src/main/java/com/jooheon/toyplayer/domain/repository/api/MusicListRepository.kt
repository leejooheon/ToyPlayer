package com.jooheon.toyplayer.domain.repository.api

import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.common.Result

interface MusicListRepository {
    suspend fun getMusicFromAsset(): Result<List<Song>, MusicDataError>
    suspend fun getLocalMusicList(): Result<List<Song>, MusicDataError>
    suspend fun getStreamingMusicList(): Result<List<Song>, MusicDataError>
    suspend fun getRadioStationList(): Result<List<Song>, MusicDataError>
}