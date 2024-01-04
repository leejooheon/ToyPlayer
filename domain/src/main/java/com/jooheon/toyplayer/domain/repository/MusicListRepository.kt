package com.jooheon.toyplayer.domain.repository

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song

interface MusicListRepository {
    suspend fun getMusicFromAsset(): Resource<MutableList<Song>>
    suspend fun getLocalMusicList(): Resource<MutableList<Song>>
    suspend fun getStreamingMusicList(): Resource<MutableList<Song>>
    fun getMusicListType(): MusicListType
    fun setMusicListType(musicListType: MusicListType)
}