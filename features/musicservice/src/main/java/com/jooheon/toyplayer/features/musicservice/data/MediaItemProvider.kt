package com.jooheon.toyplayer.features.musicservice.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.MediaFolder
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.MediaId.Companion.toMediaIdOrNull
import com.jooheon.toyplayer.domain.entity.music.Playlist
import com.jooheon.toyplayer.domain.usecase.music.library.PlaylistUseCase
import com.jooheon.toyplayer.domain.usecase.music.list.MusicListUseCase
import com.jooheon.toyplayer.features.musicservice.MusicStateHolder
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.R
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber

class MediaItemProvider(
    private val context: Context,
    private val musicListUseCase: MusicListUseCase,
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
                songs.map { it.toMediaItem(MediaId.Content(mediaId, it.key()) ) }
            }
            is MediaId.LocalSongs -> {
                val songs = musicListUseCase.getLocalSongList()
                songs.map { it.toMediaItem(MediaId.Content(mediaId, it.key()) ) }
            }
            is MediaId.StreamSongs -> {
                val songs = musicListUseCase.getStreamingUrlList()
                songs.map { it.toMediaItem(MediaId.Content(mediaId, it.key()) ) }
            }
            is MediaId.AssetSongs -> {
                val songs = musicListUseCase.getSongListFromAsset()
                songs.map { it.toMediaItem(MediaId.Content(mediaId, it.key()) ) }
            }
            is MediaId.AlbumRoot -> {
                val groupByAlbum = getAlbums()
                groupByAlbum.map { it.toMediaItem(MediaId.Album(id)) }
            }
            is MediaId.Album -> {
                val albums = getAlbums()
                val songs = albums.firstOrNull { it.id == mediaId.id }?.songs.defaultEmpty()
                songs.map { it.toMediaItem(MediaId.Content(mediaId, it.key())) }
            }
            is MediaId.PlaylistRoot -> {
                val playlists = getPlaylists()
                playlists.map { it.toMediaItem() }
            }
            is MediaId.Playlist -> {
                val playlists = getPlaylists()
                val playlist = playlists.firstOrNull { it.id.toString() == mediaId.id } ?: return emptyList()

                return playlist.songs.map { it.toMediaItem(MediaId.Content(mediaId, it.key())) }
            }
            else -> emptyList()
        }
    }

    suspend fun getSongListOrNull(mediaItem: MediaItem): Pair<List<MediaItem>?, Int> {
        Timber.d("getSongListOrNull: mediaId - ${mediaItem.mediaId}")

        val mediaId = mediaItem.mediaId.toMediaIdOrNull() ?: run {
            Timber.e("getSongListOrNull: mediaId is invalid")
            return Pair(null, C.INDEX_UNSET)
        }

        if(mediaId !is MediaId.Content) {
            Timber.e("getSongListOrNull: mediaId is not content")
            return Pair(null, C.INDEX_UNSET)
        }

        val songs = when(val parentId = mediaId.parent) {
            MediaId.AllSongs -> musicListUseCase.getAllSongList()
            is MediaId.Album -> {
                val albums = getAlbums()
                albums.firstOrNull { it.id == parentId.id }?.songs.defaultEmpty()
            }
            is MediaId.Playlist -> {
                val playlistId = parentId.id

                val playlists = getPlaylists()
                val playlist = playlists.firstOrNull { it.id.toString() == playlistId }

                playlist?.songs.defaultEmpty()
            }
            else -> emptyList()
        }

        val mediaItems = songs.map { it.toMediaItem() }
        val index = mediaItems.indexOfFirst { it.mediaId == mediaId.key }

        return Pair(mediaItems, index)
    }

    private suspend fun getAlbums(): List<Album> {
        return musicListUseCase.getAllSongList().groupBy {
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
        return playlistUseCase.allPlaylist().firstOrNull().defaultEmpty()
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