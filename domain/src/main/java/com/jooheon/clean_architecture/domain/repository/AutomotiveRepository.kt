package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.MediaFolder

interface AutomotiveRepository: BaseRepository {
    fun getMediaFolderList(): Resource<List<MediaFolder>>
}