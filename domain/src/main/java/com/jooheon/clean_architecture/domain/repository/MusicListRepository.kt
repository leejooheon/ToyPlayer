package com.jooheon.clean_architecture.domain.repository

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.MediaFolder
import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song

interface MusicListRepository {
    suspend fun getMusicFromAsset(): Resource<MutableList<Song>>
    suspend fun getLocalMusicList(uri: String): Resource<MutableList<Song>>
    suspend fun getStreamingMusicList(): Resource<MutableList<Song>>
    fun getMusicListType(): MusicListType
    fun setMusicListType(musicListType: MusicListType)
}