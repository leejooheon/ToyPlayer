package com.jooheon.clean_architecture.data.repository

import android.net.Uri
import com.jooheon.clean_architecture.data.datasource.local.MusicDataSource
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.repository.MusicRepository

class MusicRepositoryImpl(
    private val musicLocalDataSource: MusicDataSource,
): MusicRepository {
    override suspend fun getAlbums(uri: String): Resource<List<Entity.Song>> {
        val data = musicLocalDataSource.getAlbums(Uri.parse(uri))
        return Resource.Success(data)
    }

    override suspend fun getSongs(uri: String): Resource<List<Entity.Song>> {
        val data = musicLocalDataSource.getSongs(Uri.parse(uri))

        val result = if(data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data)
        }

        return result
    }
}