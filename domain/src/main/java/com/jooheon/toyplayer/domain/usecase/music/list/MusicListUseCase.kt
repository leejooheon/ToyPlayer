package com.jooheon.toyplayer.domain.usecase.music.list

import com.jooheon.toyplayer.domain.entity.music.MusicListType
import com.jooheon.toyplayer.domain.entity.music.Song
import kotlinx.coroutines.flow.Flow

interface MusicListUseCase {
    val musicListType: Flow<MusicListType>
    suspend fun getAllSongList(): List<Song>
    suspend fun getLocalSongList(): List<Song>
    suspend fun getStreamingUrlList(): List<Song>
    suspend fun getSongListFromAsset(): List<Song>

    fun getMusicListType(): MusicListType
    fun setMusicListType(musicListType: MusicListType)
}