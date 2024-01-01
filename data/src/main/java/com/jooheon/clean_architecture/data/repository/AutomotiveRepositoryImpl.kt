package com.jooheon.clean_architecture.data.repository

import com.jooheon.clean_architecture.data.datasource.local.LocalMusicDataSource
import com.jooheon.clean_architecture.domain.common.FailureStatus
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.MediaFolder
import com.jooheon.clean_architecture.domain.repository.AutomotiveRepository

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