package com.jooheon.clean_architecture.domain.usecase.music.list

import com.jooheon.clean_architecture.domain.entity.music.MusicListType

interface IMusicListUseCase {
    fun loadSongList(storageUrl: String)
    fun loadLocalSongList(storageUrl: String)
    fun loadStreamingUrlList()
    fun loadSongListFromAsset()

    fun getMusicListType(): MusicListType
    fun setMusicListType(musicListType: MusicListType)
}