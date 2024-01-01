package com.jooheon.clean_architecture.domain.usecase.music.list

import com.jooheon.clean_architecture.domain.entity.music.MusicListType
import com.jooheon.clean_architecture.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow

interface MusicListUseCase {
    val localSongList: Flow<List<Song>>
    val streamingSongList: Flow<List<Song>>
    val assetSongList: Flow<List<Song>>
    val musicListType: Flow<MusicListType>

    suspend fun initialize(storageUrl: String)
    suspend fun getLocalSongList(storageUrl: String): List<Song>
    suspend fun getStreamingUrlList(): List<Song>
    suspend fun getSongListFromAsset(): List<Song>

    fun getMusicListType(): MusicListType
    fun setMusicListType(musicListType: MusicListType)
}