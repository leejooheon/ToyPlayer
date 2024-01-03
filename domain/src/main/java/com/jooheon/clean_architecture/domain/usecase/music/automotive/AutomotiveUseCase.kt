package com.jooheon.clean_architecture.domain.usecase.music.automotive

import com.jooheon.clean_architecture.domain.entity.music.Album
import com.jooheon.clean_architecture.domain.entity.music.MediaFolder
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase

interface AutomotiveUseCase: BaseUseCase {
    fun getMediaFolderList(): List<MediaFolder>
    suspend fun getAllSongs(): List<Song>
    suspend fun getSongs(mediaId: String): List<Song>?
    fun getSong(mediaId: String): Song?
    suspend fun getAlbum(mediaId: String): Album?
    suspend fun getAlbums(): List<Album>
    suspend fun getCurrentPlayingSongs(): List<Song>
    suspend fun setCurrentDisplayedSongList(songList: List<Song>)
}