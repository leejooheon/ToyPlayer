package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datasource.local.LocalMusicDataSource
import com.jooheon.toyplayer.data.datasource.remote.RemoteMusicDataSource
import com.jooheon.toyplayer.data.datasource.local.AppPreferences
import com.jooheon.toyplayer.domain.common.FailureStatus
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.MusicListRepository

class MusicListRepositoryImpl(
    private val localMusicDataSource: LocalMusicDataSource,
    private val remoteMusicDataSource: RemoteMusicDataSource,
    private val appPreferences: AppPreferences,
): MusicListRepository {
    private val TAG = MusicListRepositoryImpl::class.java.simpleName
    override suspend fun getMusicFromAsset(): Resource<MutableList<Song>> {
        val data = localMusicDataSource.loadFromAssets()
        val result = if (data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data)
        }

        return result
    }

    override suspend fun getLocalMusicList(): Resource<MutableList<Song>> {
        val data = localMusicDataSource.getLocalMusicList()
        val result = if (data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data)
        }

        return result
    }

    override suspend fun getStreamingMusicList(): Resource<MutableList<Song>> {
        val data = remoteMusicDataSource.getStreamingMusicList()

        val resource = if(data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data.toMutableList())
        }

        return resource
    }
    override fun getMusicListType(): MusicListType {
        val ordinal = appPreferences.musicListType
        return MusicListType.entries[ordinal]
    }

    override fun setMusicListType(musicListType: MusicListType) {
        appPreferences.musicListType = musicListType.ordinal
    }
}