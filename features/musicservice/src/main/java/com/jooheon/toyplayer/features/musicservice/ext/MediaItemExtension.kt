@file:UnstableApi
package com.jooheon.toyplayer.features.musicservice.ext

import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.model.music.Album
import com.jooheon.toyplayer.domain.model.music.MediaFolder
import com.jooheon.toyplayer.domain.model.music.MediaId
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import kotlinx.serialization.json.Json
import timber.log.Timber

fun MediaItem.isHls(): Boolean = localConfiguration?.mimeType == MimeTypes.APPLICATION_M3U8

fun Song.toMediaItem(): MediaItem {
    val uri = if(isRadio) radioData()!!.serialize().toUri() else uri
    val customCacheKey = if(isRadio) radioData()!!.serialize() else key()
    val imageUri = if(isRadio) radioData()!!.imageUrl.toUri() else albumArtUri
    val mimeType = if(isRadio) MimeTypes.APPLICATION_M3U8 else null
    val mediaType = if(isRadio) MediaMetadata.MEDIA_TYPE_PODCAST else MediaMetadata.MEDIA_TYPE_MUSIC
    val mediaMetadata = toMetadata(mediaType, imageUri)

    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(key())
        .setCustomCacheKey(customCacheKey)
        .setMediaMetadata(mediaMetadata)
        .setMimeType(mimeType)
        .build()
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


fun MediaFolder.toMediaBrowsableItem(): MediaItem {
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
        .setMediaId(Json.encodeToString(MediaId.serializer(), mediaId))
        .setMediaMetadata(metadata)
        .setSubtitleConfigurations(mutableListOf())
        .setUri(Uri.EMPTY)
        .build()
}

private fun RadioData.toMediaItem(index: Int = 0): MediaItem {
    Timber.d("toMediaItem: $channelName")
    val title = "${type.name()} Radio"

    val metadata = MediaMetadata.Builder()
        .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST)
        .setDisplayTitle(channelName)
        .setTitle(channelName)
        .setAlbumTitle(title)
        .setAlbumArtist(title)
        .setArtist(type.name())
        .setArtworkUri(null)
        .setTrackNumber(index)
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .build()

    return MediaItem.Builder()
        .setUri(Uri.EMPTY)
        .setMediaId(mediaId())
        .setCustomCacheKey(serialize())
        .setMediaMetadata(metadata)
        .setMimeType(MimeTypes.APPLICATION_M3U8)
        .build()
}