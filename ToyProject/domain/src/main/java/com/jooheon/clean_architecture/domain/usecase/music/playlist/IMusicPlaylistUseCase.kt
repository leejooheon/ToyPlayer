package com.jooheon.clean_architecture.domain.usecase.music.playlist

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.music.Playlist
import com.jooheon.clean_architecture.domain.usecase.BaseUseCase
import kotlinx.coroutines.flow.Flow

interface IMusicPlaylistUseCase: BaseUseCase {
    fun update()

    fun getAllPlaylist(): Flow<Resource<List<Playlist>>>
    fun getPlaylist(id: Int): Flow<Resource<Playlist>>

    suspend fun updatePlaylists(vararg playlist: Playlist)
    suspend fun insertPlaylists(vararg playlist: Playlist)
    suspend fun deletePlaylists(vararg playlist: Playlist)
}