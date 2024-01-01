package com.jooheon.clean_architecture.domain.usecase.music.automotive

import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.MediaFolder
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.repository.AutomotiveRepository
import com.jooheon.clean_architecture.domain.repository.MusicListRepository
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class AutomotiveUseCaseImpl(
    private val automotiveRepository: AutomotiveRepository,
    private val musicListUseCase: MusicListUseCase,
): AutomotiveUseCase {
    private var currentPlayingQueue: List<Song> = emptyList()

    override fun getMediaFolderList(): List<MediaFolder> {
        val resource = automotiveRepository.getMediaFolderList()

        if(resource is Resource.Success) {
            return resource.value
        }

        return emptyList()
    }


    override suspend fun getAllSongs(storageUrl: String): List<Song> {
        val allSongs = mutableListOf<Song>()
        musicListUseCase.getLocalSongList(storageUrl).run {
            allSongs.addAll(this)
        }
        musicListUseCase.getStreamingUrlList().run {
            allSongs.addAll(this)
        }
        musicListUseCase.getSongListFromAsset().run {
            allSongs.addAll(this)
        }

        return allSongs
    }

    override suspend fun getSongs(mediaId: String): List<Song>? {
        listOf(
            musicListUseCase.localSongList,
            musicListUseCase.streamingSongList,
            musicListUseCase.assetSongList
        ).forEach { songListFlow ->
            val songList = songListFlow.firstOrNull().defaultEmpty()
            val song = songList.firstOrNull { it.key() == mediaId }

            currentPlayingQueue = songList

            if(song != null) return songList
        }

        return null
    }

    override suspend fun getSong(mediaId: String): Song? {
        listOf(
            musicListUseCase.localSongList,
            musicListUseCase.streamingSongList,
            musicListUseCase.assetSongList
        ).forEach { songListFlow ->
            val songList = songListFlow.firstOrNull().defaultEmpty()
            val song = songList.firstOrNull { it.key() == mediaId }

            if(song != null) return song
        }
        return null
    }

    override suspend fun getCurrentPlayingSongs(): List<Song> = currentPlayingQueue
}