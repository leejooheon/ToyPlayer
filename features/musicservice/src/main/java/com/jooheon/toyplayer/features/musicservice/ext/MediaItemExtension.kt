@file:UnstableApi
package com.jooheon.toyplayer.features.musicservice.ext

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.C
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
import kotlinx.serialization.json.Json
import timber.log.Timber

fun Song.toMediaItem(): MediaItem {
    return if(isHlsFormat) {
        toHlsMediaItemInternal()
    } else {
        toMediaItemInternal()
    }
}

private fun Song.toMediaItemInternal(): MediaItem {
    val metadata = toMetadata(MediaMetadata.MEDIA_TYPE_MUSIC)

    return MediaItem.Builder()
        .setUri(key())
        .setMediaId(key())
        .setCustomCacheKey(key())
        .setMediaMetadata(metadata)
        .build()
}

private fun Song.toHlsMediaItemInternal(): MediaItem {
    Timber.d("toHlsMediaItem: $uri")
    val metadata = toMetadata(MediaMetadata.MEDIA_TYPE_PODCAST)
    return MediaItem.Builder()
        .setUri(uri)
        .setMediaId(key())
        .setCustomCacheKey(key())
        .setMediaMetadata(metadata)
        .setMimeType(MimeTypes.APPLICATION_M3U8)
        .build()
}

private fun Song.toMetadata(mediaType: Int): MediaMetadata {
    val metadata = MediaMetadata.Builder()
        .setMediaType(mediaType)
        .setDisplayTitle(displayName)
        .setTitle(title)
        .setAlbumTitle(album)
        .setAlbumArtist(artist)
        .setArtist(artist)
        .setArtworkUri(albumArtUri)
        .setTrackNumber(trackNumber)
        .setExtras(extras())
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .build()

    return metadata
}

fun MediaItem.toSong(): Song {
    fun getUseCache(extras: Bundle?): Boolean = extras?.getBoolean(Song.BUNDLE_USE_CACHE).defaultFalse()
    fun getArtistId(extras: Bundle?): String = extras?.getString(Song.BUNDLE_ARTIST_ID).defaultEmpty()
    fun getAlbumId(extras: Bundle?): String = extras?.getString(Song.BUNDLE_ALBUM_ID).defaultEmpty()
    fun getDuration(extras: Bundle?): Long = extras?.getLong(Song.BUNDLE_DURATION).defaultZero()
    fun getIsFavorite(extras: Bundle?): Boolean = extras?.getBoolean(Song.BUNDLE_IS_FAVORITE).defaultFalse()
    fun getData(extras: Bundle?): String? = extras?.getString(Song.BUNDLE_DATA)
    fun getPath(extras: Bundle?): String = extras?.getString(Song.BUNDLE_PATH).defaultEmpty()

    val audioId = mediaId.toLongOrNull()

    return with(mediaMetadata) {
        Song(
            audioId = audioId.defaultZero(),
            useCache = getUseCache(extras),
            displayName = displayTitle.toString(),
            title = title.toString(),
            artist = artist.toString(),
            artistId = getArtistId(extras),
            album = albumTitle.toString(),
            albumId = getAlbumId(extras),
            duration = getDuration(extras),
            path = getPath(extras),
            trackNumber = trackNumber ?: C.INDEX_UNSET,
            imageUrl = artworkUri.toString(),
            isFavorite = getIsFavorite(extras),
            data = getData(extras)
        )
    }
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

fun MediaItem.invalidate(): MediaItem {
    if(localConfiguration != null) return this
    val song = this.toSong()


    val uri = if(song.isHlsFormat) song.uri.toString() else song.key()
    val mimeType = if(song.isHlsFormat) MimeTypes.APPLICATION_M3U8 else null
    val mediaMetadata = if(song.isHlsFormat) {
        mediaMetadata.buildUpon()
            .setMediaType(MediaMetadata.MEDIA_TYPE_PODCAST)
            .build()
    } else mediaMetadata

    return buildUpon()
        .setUri(uri)
        .setMediaId(song.key())
        .setCustomCacheKey(song.key())
        .setMediaMetadata(mediaMetadata)
        .setMimeType(mimeType)
        .build()
}