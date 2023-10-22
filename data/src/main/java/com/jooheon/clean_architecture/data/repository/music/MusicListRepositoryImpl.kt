package com.jooheon.clean_architecture.data.repository.music

import android.net.Uri
import com.jooheon.clean_architecture.data.datasource.local.LocalMusicDataSource
import com.jooheon.clean_architecture.data.datasource.remote.RemoteMusicDataSource
import com.jooheon.clean_architecture.data.datasource.local.AppPreferences
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.MusicListRepository

class MusicListRepositoryImpl(
    private val localMusicDataSource: LocalMusicDataSource,
    private val remoteMusicDataSource: RemoteMusicDataSource,
    private val appPreferences: AppPreferences,
): MusicListRepository {
    private val TAG = MusicListRepositoryImpl::class.java.simpleName

    override suspend fun getLocalMusicList(uri: String): Resource<MutableList<Song>> {
        val data = localMusicDataSource.getLocalMusicList(Uri.parse(uri))
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