package com.jooheon.toyplayer.features.musicservice.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.map
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaFolder
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.MediaId.Companion.toMediaIdOrNull
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.usecase.MusicListUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.RadioUseCase
import com.jooheon.toyplayer.features.musicservice.R
import com.jooheon.toyplayer.features.musicservice.ext.toMediaBrowsableItem
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaItemProvider(
    private val context: Context,
    private val musicListUseCase: MusicListUseCase,
    private val radioUseCase: RadioUseCase,
    private val playlistUseCase: PlaylistUseCase,
) {
    val rootItem = MediaFolder(
        title = context.getString(R.string.media_folder_root),
        mediaId = MediaId.Root,
        mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
    ).toMediaBrowsableItem()

    suspend fun getChildMediaItems(
        id: String
    ): Result<List<MediaItem>, PlaybackDataError> = withContext(Dispatchers.IO) {
        return@withContext when(val mediaId = id.toMediaIdOrNull()) {
            is MediaId.Root -> {
                val allSongs = MediaFolder(
                    title = context.getString(R.string.media_folder_all_songs),
                    mediaId = MediaId.AllSongs,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
                ).toMediaBrowsableItem()
                val playlist = MediaFolder(
                    title = context.getString(R.string.media_folder_playlist),
                    mediaId = MediaId.PlaylistRoot,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
                ).toMediaBrowsableItem()
                val album = MediaFolder(
                    title = context.getString(R.string.media_folder_album),
                    mediaId = MediaId.AlbumRoot,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
                ).toMediaBrowsableItem()

                val mediaItems = listOf(allSongs, playlist, album)
                Result.Success(mediaItems)
            }
            is MediaId.AllSongs -> {
                val songs = musicListUseCase.getAllSongList()
                val radios = radioUseCase.getRadioStationList()
                val mediaItems = (songs + radios).map { it.toMediaItem(mediaId.serialize()) }
                Result.Success(mediaItems)
            }
            is MediaId.AlbumRoot -> {
                val groupByAlbum = getAlbums()
                val mediaItems = groupByAlbum.map { it.toMediaItem() }
                Result.Success(mediaItems)
            }
            is MediaId.Album -> {
                val albums = getAlbums()
                val mediaItems = albums
                    .firstOrNull { it.id == mediaId.id }
                    ?.let { it.songs.map { it.toMediaItem(mediaId.serialize()) } }
                    .defaultEmpty()
                Result.Success(mediaItems)
            }
            is MediaId.PlaylistRoot -> {
                val result = playlistUseCase.getAllPlaylist()
                when(result) {
                    is Result.Success -> {
                        val mediaItems = result.data.map { it.toMediaItem() }
                        Result.Success(mediaItems)
                    }
                    is Result.Error -> result
                }
            }
            is MediaId.Playlist -> {
                val result = playlistUseCase.getPlaylist(mediaId.id)
                when(result) {
                    is Result.Success -> {
                        val playlist = result.data
                        val mediaItems = playlist.songs.defaultEmpty().map { it.toMediaItem(mediaId.serialize()) }
                        Result.Success(mediaItems)
                    }
                    is Result.Error -> result
                }
            }
            is MediaId.InternalMediaId -> {
                when(mediaId) {
                    is MediaId.InternalMediaId.LocalSongs -> {
                        val songs = musicListUseCase.getLocalSongList()
                        val mediaItems = songs.map { it.toMediaItem(mediaId.serialize()) }
                        Result.Success(mediaItems)
                    }
                    is MediaId.InternalMediaId.StreamSongs -> {
                        val songs = musicListUseCase.getStreamingUrlList()
                        val mediaItems = songs.map { it.toMediaItem(mediaId.serialize()) }
                        Result.Success(mediaItems)
                    }
                    is MediaId.InternalMediaId.AssetSongs -> {
                        val songs = musicListUseCase.getSongListFromAsset()
                        val mediaItems = songs.map { it.toMediaItem(mediaId.serialize()) }
                        Result.Success(mediaItems)
                    }
                    is MediaId.InternalMediaId.RadioSongs -> {
                        val songs = radioUseCase.getRadioStationList()
                        val mediaItems = songs.map { it.toMediaItem(mediaId.serialize()) }
                        Result.Success(mediaItems)
                    }
                }
            }
            else -> Result.Error(PlaybackDataError.InvalidData("Invalid media id: $id"))
        }
    }

    private suspend fun getAlbums(): List<Album> {
        val songs = musicListUseCase.getAllSongList()
        val radios = radioUseCase.getRadioStationList()

        return (songs + radios).groupBy {
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
    }

    private suspend fun getPlaylists(): List<Playlist> {
        val result = playlistUseCase.getAllPlaylist()
        return when(result) {
            is Result.Success -> result.data
            is Result.Error -> emptyList()
        }
    }

    private suspend fun getPlaylist(id: Int): Playlist? {
        val result = playlistUseCase.getPlaylist(id)
        return when(result) {
            is Result.Success -> result.data
            is Result.Error -> null
        }
    }
}