package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.MediaFolder

interface AutomotiveRepository: BaseRepository {
    fun getMediaFolderList(): Resource<List<MediaFolder>>
}