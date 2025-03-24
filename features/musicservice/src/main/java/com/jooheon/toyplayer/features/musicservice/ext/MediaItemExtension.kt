@file:UnstableApi
package com.jooheon.toyplayer.features.musicservice.ext

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaFolder
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_ALBUM_ID
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_ARTIST_ID
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_DATA
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_DURATION
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_IS_FAVORITE
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_PATH
import com.jooheon.toyplayer.domain.model.music.Song.Companion.BUNDLE_USE_CACHE

fun MediaItem.isHls(): Boolean = localConfiguration?.mimeType == MimeTypes.APPLICATION_M3U8

fun Song.toMediaItem(parentId: String): MediaItem {
    val uri = if(isRadio) radioDataOrNull()!!.serialize().toUri() else uri
    val customCacheKey = if(isRadio) radioDataOrNull()!!.serialize() else key()
    val imageUri = if(isRadio) radioDataOrNull()!!.imageUrl.toUri() else albumArtUri
    val mimeType = if(isRadio) MimeTypes.APPLICATION_M3U8 else null
    val mediaType = if(isRadio) MediaMetadata.MEDIA_TYPE_PODCAST else MediaMetadata.MEDIA_TYPE_MUSIC
    val mediaMetadata = toMetadata(mediaType, imageUri)

    val mediaId = MediaId.PlaylistMediaId(
        parentId = parentId,
        id = key()
    )

    val mediaItemBuilder = MediaItem.Builder()
        .setUri(uri)
        .setMediaId(mediaId.serialize())
        .setCustomCacheKey(customCacheKey)
        .setMediaMetadata(mediaMetadata)
        .setMimeType(mimeType)

    if(isRadio) {
        val liveConfiguration = MediaItem.LiveConfiguration.Builder()
            .setMaxPlaybackSpeed(1.1f)
            .build()
        mediaItemBuilder.setLiveConfiguration(liveConfiguration).build()
    }

    return mediaItemBuilder.build()
}

private fun Song.toMetadata(mediaType: Int, imageUri: Uri): MediaMetadata {
    val metadata = MediaMetadata.Builder()
        .setMediaType(mediaType)
        .setDisplayTitle(displayName)
        .setTitle(title)
        .setAlbumTitle(album)
        .setAlbumArtist(artist)
        .setArtist(artist)
        .setArtworkUri(imageUri)
        .setTrackNumber(trackNumber)
        .setExtras(extras())
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .build()

    return metadata
}

fun Playlist.toMediaItem(): MediaItem {
    val metadata = MediaMetadata.Builder()
        .setTitle(name)
        .setIsBrowsable(true)
        .setIsPlayable(false)
        .setMediaType(MediaMetadata.MEDIA_TYPE_PLAYLIST)
        .build()

    return MediaItem.Builder()
        .setMediaId(MediaId.Playlist(id.toString()).serialize())
        .setMediaMetadata(metadata)
        .setSubtitleConfigurations(mutableListOf())
        .setUri(Uri.EMPTY)
        .build()
}

fun Album.toMediaItem(): MediaItem {
    val mediaId = MediaId.Album(id)
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

internal fun MediaFolder.toMediaBrowsableItem(): MediaItem {
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

fun MediaItem.getUseCache(): Boolean = mediaMetadata.extras?.getBoolean(BUNDLE_USE_CACHE).defaultFalse()
fun MediaItem.getArtistId(): String = mediaMetadata.extras?.getString(BUNDLE_ARTIST_ID).defaultEmpty()
fun MediaItem.getAlbumId(): String = mediaMetadata.extras?.getString(BUNDLE_ALBUM_ID).defaultEmpty()
fun MediaItem.getDuration(): Long = mediaMetadata.extras?.getLong(BUNDLE_DURATION).defaultZero()
fun MediaItem.getIsFavorite(): Boolean = mediaMetadata.extras?.getBoolean(BUNDLE_IS_FAVORITE).defaultFalse()
fun MediaItem.getData(): String? = mediaMetadata.extras?.getString(BUNDLE_DATA)
fun MediaItem.getPath(): String = mediaMetadata.extras?.getString(BUNDLE_PATH).defaultEmpty()