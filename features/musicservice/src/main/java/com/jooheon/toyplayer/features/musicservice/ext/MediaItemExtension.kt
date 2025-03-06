package com.jooheon.toyplayer.features.musicservice.ext

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.common.extension.defaultFalse
import com.jooheon.toyplayer.domain.model.common.extension.defaultZero
import com.jooheon.toyplayer.domain.model.music.MediaId.Companion.toMediaIdOrNull
import com.jooheon.toyplayer.domain.model.music.Playlist
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.serialization.json.Json

fun Song.toMediaItem(): MediaItem {
    val metadata = MediaMetadata.Builder()
        .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
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

    return MediaItem.Builder()
        .setUri(key())
        .setMediaId(key())
        .setCustomCacheKey(key())
        .setMediaMetadata(metadata)
        .build()
}

fun Song.toMediaItem(mediaId: com.jooheon.toyplayer.domain.model.music.MediaId): MediaItem {
    val key = mediaId.serialize()
    val metadata = MediaMetadata.Builder()
        .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
        .setDisplayTitle(displayName)
        .setTitle(title)
        .setAlbumTitle(album)
        .setAlbumArtist(artist)
        .setArtist(artist)
        .setArtworkUri(albumArtUri)
        .setExtras(extras())
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .build()

    return MediaItem.Builder()
        .setUri(key)
        .setMediaId(key)
        .setCustomCacheKey(key)
        .setMediaMetadata(metadata).build()
}

fun MediaItem.toSong(): Song {
    fun getUseCache(extras: Bundle?): Boolean = extras?.getBoolean(Song.BUNDLE_USE_CACHE).defaultFalse()
    fun getArtistId(extras: Bundle?): String = extras?.getString(Song.BUNDLE_ARTIST_ID).defaultEmpty()
    fun getAlbumId(extras: Bundle?): String = extras?.getString(Song.BUNDLE_ALBUM_ID).defaultEmpty()
    fun getDuration(extras: Bundle?): Long = extras?.getLong(Song.BUNDLE_DURATION).defaultZero()
    fun getIsFavorite(extras: Bundle?): Boolean = extras?.getBoolean(Song.BUNDLE_IS_FAVORITE).defaultFalse()
    fun getData(extras: Bundle?): String? = extras?.getString(Song.BUNDLE_DATA)
    fun getPath(extras: Bundle?): String = extras?.getString(Song.BUNDLE_PATH).defaultEmpty()

    val id = mediaId.toMediaIdOrNull() ?: mediaId.toLongOrNull()
    val audioId = if(id is com.jooheon.toyplayer.domain.model.music.MediaId.Content) id.key.toLongOrNull()
                  else mediaId.toLongOrNull()

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
        .setMediaId(com.jooheon.toyplayer.domain.model.music.MediaId.Playlist(id.toString()).serialize())
        .setMediaMetadata(metadata)
        .setSubtitleConfigurations(mutableListOf())
        .setUri(Uri.EMPTY)
        .build()
}


fun com.jooheon.toyplayer.domain.model.music.MediaFolder.toMediaBrowsableItem(): MediaItem {
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

fun com.jooheon.toyplayer.domain.model.music.Album.toMediaItem(): MediaItem {
    val mediaId = com.jooheon.toyplayer.domain.model.music.MediaId.Album(id)
    val metadata = MediaMetadata.Builder()
        .setTitle(name)
        .setIsBrowsable(true)
        .setIsPlayable(false)
        .setArtworkUri(imageUrl.toUri())
        .setMediaType(MediaMetadata.MEDIA_TYPE_ALBUM)
        .build()

    return MediaItem.Builder()
        .setMediaId(Json.encodeToString(com.jooheon.toyplayer.domain.model.music.MediaId.serializer(), mediaId))
        .setMediaMetadata(metadata)
        .setSubtitleConfigurations(mutableListOf())
        .setUri(Uri.EMPTY)
        .build()
}