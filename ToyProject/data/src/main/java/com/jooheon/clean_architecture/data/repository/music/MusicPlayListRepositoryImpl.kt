package com.jooheon.clean_architecture.data.repository.music

import android.net.Uri
import com.jooheon.clean_architecture.data.datasource.local.LocalMusicPlayListDataSource
import com.jooheon.clean_architecture.data.datasource.remote.RemoteMusicPlayListDataSource
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.MusicPlayListRepository

class MusicPlayListRepositoryImpl(
    private val localMusicPlayListDataSource: LocalMusicPlayListDataSource,
    private val remoteMusicPlayListDataSource: RemoteMusicPlayListDataSource,
): MusicPlayListRepository {
    private val TAG = MusicPlayListRepositoryImpl::class.java.simpleName

    override suspend fun getLocalSongList(uri: String): Resource<MutableList<Song>> {
        val data = localMusicPlayListDataSource.getLocalSongList(Uri.parse(uri))
        val result = if (data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data)
        }

        return result
    }

    override suspend fun getStreamingUrlList(): Resource<MutableList<Song>> {
        val data = remoteMusicPlayListDataSource.getStreamingUrlList()

        val resource = if(data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data.toMutableList())
        }

        return resource
    }
}