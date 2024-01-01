package com.jooheon.clean_architecture.features.musicservice.data

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.MediaFolder
import com.jooheon.clean_architecture.domain.entity.music.MediaId
import com.jooheon.clean_architecture.domain.usecase.music.automotive.AutomotiveUseCase
import com.jooheon.clean_architecture.features.musicservice.ext.toMediaItem
import com.jooheon.clean_architecture.toyproject.features.common.utils.MusicUtil
import com.jooheon.clean_architecture.toyproject.features.musicservice.R
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class MediaItemProvider(
    private val context: Context,
    private val automotiveUseCase: AutomotiveUseCase,
) {
    suspend fun getChildBrowsableItems(mediaId: String): List<MediaItem> {
        return when(mediaId.toMediaIdOrNull()) {
            is MediaId.AutomotiveRoot -> automotiveUseCase.getMediaFolderList().map { it.toMediaBrowsableItem() }
            is MediaId.AllSongs -> {
                val uri = MusicUtil.localMusicStorageUri().toString()
                automotiveUseCase.getAllSongs(uri).map { it.toMediaItem() }
            }
            is MediaId.Album -> emptyList() // TODO
            is MediaId.Playlist -> emptyList() // TODO
            else -> emptyList()
        }
    }

    suspend fun getItem(mediaId: String): MediaItem? {
        return when(mediaId.toMediaIdOrNull()) {
            is MediaId.AutomotiveRoot -> rootItem()
            else -> automotiveUseCase.getSong(mediaId)?.toMediaItem()
        }
    }

    @UnstableApi
    suspend fun mediaItemsWithStartPosition(mediaId: String): MediaSession.MediaItemsWithStartPosition? {
        return when(mediaId.toMediaIdOrNull()) {
            is MediaId.AutomotiveRoot,
            is MediaId.Album -> null
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
        mediaId = MediaId.AutomotiveRoot,
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

    private fun String.toMediaIdOrNull(): MediaId? = try {
        Json.decodeFromString(MediaId.serializer(), this)
    } catch (e: SerializationException) {
        null
    }

    private fun MediaId.toStringIdOrNull() = Json.encodeToString(MediaId.serializer(), this)
}