package com.jooheon.clean_architecture.domain.usecase.music.list

import com.jooheon.clean_architecture.domain.entity.music.MusicListType

interface IMusicListUseCase {
    suspend fun loadSongList(storageUrl: String)
    suspend fun loadLocalSongList(storageUrl: String)
    suspend fun loadStreamingUrlList()
    suspend fun loadSongListFromAsset()

    fun getMusicListType(): MusicListType
    fun setMusicListType(musicListType: MusicListType)
}