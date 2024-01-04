package com.jooheon.toyplayer.domain.usecase.music.automotive

import com.jooheon.toyplayer.domain.common.Resource
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.MediaFolder
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.repository.AutomotiveRepository
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
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


    override suspend fun getAllSongs(): List<Song> {
        val allSongs = mutableListOf<Song>()
        musicListUseCase.getLocalSongList().run {
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
            if(song != null) return songList
        }

        return null
    }

    override fun getSong(mediaId: String): Song? {
        return currentPlayingQueue.firstOrNull { it.key() == mediaId }
    }

    override suspend fun getAlbums(): List<Album> {
        val groupByAlbum = getAllSongs().groupBy {
            it.albumId
        }.map { (albumId, songs) ->
            Album(
                id = albumId,
                name = songs.firstOrNull()?.album.defaultEmpty(),
                artist = songs.firstOrNull()?.artist.defaultEmpty(),
                artistId = songs.firstOrNull()?.artistId.defaultEmpty(),
                imageUrl = songs.firstOrNull()?.imageUrl.defaultEmpty(),
                songs = songs.sortedBy { it.trackNumber }
            )
        }
        return groupByAlbum
    }

    override suspend fun getAlbum(mediaId: String): Album? {
        val albums = getAlbums()
        val album = albums.firstOrNull {
            it.id == mediaId
        }

        return album
    }

    override suspend fun getCurrentPlayingSongs(): List<Song> = currentPlayingQueue
    override suspend fun setCurrentDisplayedSongList(songList: List<Song>) {
        songList.filter { song ->
            currentPlayingQueue.none { it.key() == song.key() } // remove duplicate
        }.also {
            val new = currentPlayingQueue.toMutableList().apply {
                addAll(it)
            }
            currentPlayingQueue = new
        }
    }
}