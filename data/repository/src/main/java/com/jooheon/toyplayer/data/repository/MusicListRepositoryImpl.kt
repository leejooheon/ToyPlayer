package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.music.LocalMusicDataSource
import com.jooheon.toyplayer.data.music.RemoteMusicDataSource
import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.repository.api.MusicListRepository
import com.jooheon.toyplayer.domain.model.common.Result

class MusicListRepositoryImpl(
    private val localMusicDataSource: LocalMusicDataSource,
    private val remoteMusicDataSource: RemoteMusicDataSource,
): MusicListRepository {
    override suspend fun getMusicFromAsset(): Result<List<Song>, MusicDataError> {
        val data = localMusicDataSource.loadFromAssets()
        return if (data.isEmpty()) {
            Result.Error(MusicDataError.Empty)
        } else {
            Result.Success(data)
        }
    }

    override suspend fun getLocalMusicList(): Result<List<Song>, MusicDataError> {
        val data = localMusicDataSource.getLocalMusicList()
        return if (data.isEmpty()) {
            Result.Error(MusicDataError.Empty)
        } else {
            Result.Success(data)
        }
    }

    override suspend fun getStreamingMusicList(): Result<List<Song>, MusicDataError> {
        val data = remoteMusicDataSource.getStreamingMusicList()
        return if (data.isEmpty()) {
            Result.Error(MusicDataError.Empty)
        } else {
            Result.Success(data)
        }
    }
}