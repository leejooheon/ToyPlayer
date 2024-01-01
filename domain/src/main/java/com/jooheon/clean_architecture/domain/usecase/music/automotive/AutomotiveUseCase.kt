package com.jooheon.clean_architecture.domain.usecase.music.automotive

import com.jooheon.clean_architecture.domain.entity.music.MediaFolder
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase

interface AutomotiveUseCase: BaseUseCase {
    fun getMediaFolderList(): List<MediaFolder>
    suspend fun getAllSongs(storageUrl: String): List<Song>
    suspend fun getSongs(mediaId: String): List<Song>?
    suspend fun getSong(mediaId: String): Song?
    suspend fun getCurrentPlayingSongs(): List<Song>
}