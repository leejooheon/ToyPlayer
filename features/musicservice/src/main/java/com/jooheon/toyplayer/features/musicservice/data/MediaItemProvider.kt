package com.jooheon.toyplayer.features.musicservice.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.Album
import com.jooheon.toyplayer.domain.entity.music.MediaFolder
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.toyplayer.features.musicservice.ext.toMediaItem
import com.jooheon.toyplayer.features.musicservice.R
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class MediaItemProvider(
    private val context: Context,
    private val automotiveUseCase: AutomotiveUseCase,
) {
    suspend fun getChildBrowsableItems(id: String): List<MediaItem> {
        return when(val mediaId = id.toMediaIdOrNull()) {
            is MediaId.Root -> automotiveUseCase.getMediaFolderList().map { it.toMediaBrowsableItem() }
            is MediaId.AllSongs -> {
                val songList = automotiveUseCase.getAllSongs()
                songList.map { it.toMediaItem() }
            }
            is MediaId.AlbumRoot -> automotiveUseCase.getAlbums().map { it.toMediaItem()}
            is MediaId.Album -> {
                val songList = automotiveUseCase.getAlbum(mediaId.id)?.songs.defaultEmpty()
                songList.map { it.toMediaItem() }
            }
            is MediaId.Playlist -> emptyList() // TODO
            else -> emptyList()
        }
    }
    suspend fun setCurrentDisplayedSongList(mediaItems: List<MediaItem>) {
        val allSongs = automotiveUseCase.getAllSongs()
        val songList = mediaItems.mapNotNull { mediaItem ->
            allSongs.firstOrNull {
                mediaItem.mediaId == it.key()
            }
        }
        automotiveUseCase.setCurrentDisplayedSongList(songList)
    }

    fun getItem(mediaId: String): MediaItem? {
        return when(mediaId.toMediaIdOrNull()) {
            is MediaId.Root -> rootItem()
            else -> {
                automotiveUseCase.getSong(mediaId)?.toMediaItem()
            }
        }
    }

    @UnstableApi
    suspend fun mediaItemsWithStartPosition(mediaId: String): MediaSession.MediaItemsWithStartPosition? {
        return when(mediaId.toMediaIdOrNull()) {
            is MediaId.Root,
            is MediaId.AlbumRoot -> null
            else -> {
                val songs = automotiveUseCase.getSongs(mediaId) ?: return null
                val startIndex = songs.indexOfFirst { it.key() == mediaId }

                MediaSession.MediaItemsWithStartPosition(
                    songs.map { it.toMediaItem() },
                    startIndex,
                    C.TIME_UNSET,
                )
            }
        }
    }

    fun rootItem() = MediaFolder(
        title = context.getString(R.string.media_folder_root),
        mediaId = MediaId.Root,
        mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
    ).toMediaBrowsableItem()

    private fun MediaFolder.toMediaBrowsableItem(): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setMediaType(mediaType)
            .build()

        return MediaItem.Builder()
            .setMediaId(mediaId.toStringIdOrNull().defaultEmpty())
            .setMediaMetadata(metadata)
            .setSubtitleConfigurations(mutableListOf())
            .setUri(Uri.EMPTY)
            .build()
    }

    private fun Album.toMediaItem(): MediaItem {
        val mediaId = MediaId.Album(id)
        val metadata = MediaMetadata.Builder()
            .setTitle(name)
            .setIsBrowsable(true)
            .setIsPlayable(false)
            .setArtworkUri(imageUrl.toUri())
            .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
            .build()

        return MediaItem.Builder()
            .setMediaId(Json.encodeToString(MediaId.serializer(), mediaId))
            .setMediaMetadata(metadata)
            .setSubtitleConfigurations(mutableListOf())
            .setUri(Uri.EMPTY)
            .build()
    }

    private fun String.toMediaIdOrNull(): MediaId? = try {
        Json.decodeFromString(MediaId.serializer(), this)
    } catch (e: SerializationException) {
        null
    }

    private fun MediaId.toStringIdOrNull() = Json.encodeToString(MediaId.serializer(), this)

}