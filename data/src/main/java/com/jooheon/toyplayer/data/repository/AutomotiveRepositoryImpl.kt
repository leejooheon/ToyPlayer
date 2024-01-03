package com.jooheon.toyplayer.data.repository

import com.jooheon.toyplayer.data.datasource.local.LocalMusicDataSource
import com.jooheon.toyplayer.domain.common.FailureStatus
import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.MediaFolder
import com.jooheon.toyplayer.domain.repository.AutomotiveRepository

class AutomotiveRepositoryImpl(
    private val localMusicDataSource: LocalMusicDataSource,
): AutomotiveRepository {
    override fun getMediaFolderList(): Resource<List<MediaFolder>> {
        val data = localMusicDataSource.getMediaFolderList()

        val resource = if(data.isEmpty()) {
            Resource.Failure(FailureStatus.EMPTY)
        } else {
            Resource.Success(data)
        }

        return resource
    }
}