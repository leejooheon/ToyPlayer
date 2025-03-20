package com.jooheon.toyplayer.features.musicservice.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.domain.model.common.Result
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
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem

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

    suspend fun getChildMediaItems(id: String): List<MediaItem> {
        return when(val mediaId = id.toMediaIdOrNull()) {
            is MediaId.Root -> {
                val allSongs = MediaFolder(
                    title = context.getString(R.string.media_folder_all_songs),
                    mediaId = MediaId.AllSongs,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
                ).toMediaBrowsableItem()
                val album = MediaFolder(
                    title = context.getString(R.string.media_folder_album),
                    mediaId = MediaId.AlbumRoot,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
                ).toMediaBrowsableItem()
                val playlist = MediaFolder(
                    title = context.getString(R.string.media_folder_playlist),
                    mediaId = MediaId.PlaylistRoot,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
                ).toMediaBrowsableItem()

                listOf(allSongs, album, playlist)
            }
            is MediaId.AllSongs -> {
                val songs = musicListUseCase.getAllSongList()
                val radios = radioUseCase.getRadioStationList()
                (songs + radios).map { it.toMediaItem() }
            }
            is MediaId.LocalSongs -> {
                val songs = musicListUseCase.getLocalSongList()
                songs.map { it.toMediaItem() }
            }
            is MediaId.StreamSongs -> {
                val songs = musicListUseCase.getStreamingUrlList()
                songs.map { it.toMediaItem() }
            }
            is MediaId.AssetSongs -> {
                val songs = musicListUseCase.getSongListFromAsset()
                songs.map { it.toMediaItem() }
            }
            is MediaId.RadioSongs -> {
                val songs = radioUseCase.getRadioStationList()
                songs.map { it.toMediaItem() }
            }
            is MediaId.AlbumRoot -> {
                val groupByAlbum = getAlbums()
                groupByAlbum.map { it.toMediaItem(MediaId.Album(id)) }
            }
            is MediaId.Album -> {
                val albums = getAlbums()
                val songs = albums.firstOrNull { it.id == mediaId.id }?.songs.defaultEmpty()
                songs.map { it.toMediaItem() }
            }
            is MediaId.PlaylistRoot -> {
                val playlists = getPlaylists()
                playlists.map { it.toMediaItem() }
            }
            is MediaId.Playlist -> {
                val playlists = getPlaylists()
                val playlist = playlists.firstOrNull { it.id.toString() == mediaId.id } ?: return emptyList()

                playlist.songs.map { it.toMediaItem() }
            }
            else -> emptyList()
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

    private fun MediaFolder.toMediaBrowsableItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setMediaType(mediaType)
            .build()

        return MediaItem.Builder()
            .setMediaId(mediaId.serialize())
            .setMediaMetadata(metadata)
            .setSubtitleConfigurations(mutableListOf())
            .setUri(Uri.EMPTY)
            .build()
    }

    private fun Album.toMediaItem(mediaId: MediaId): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(name)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setArtworkUri(imageUrl.toUri())
            .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
            .build()

        return MediaItem.Builder()
            .setMediaId(mediaId.serialize())
            .setMediaMetadata(metadata)
            .setSubtitleConfigurations(mutableListOf())
            .setUri(Uri.EMPTY)
            .build()
    }
}