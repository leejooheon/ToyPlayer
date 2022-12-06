package com.jooheon.clean_architecture.presentation.utils

import android.content.ContentUris
import android.media.MediaMetadata
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.base.extensions.albumArtUri
import com.jooheon.clean_architecture.presentation.base.extensions.uri
import com.jooheon.clean_architecture.presentation.service.music.MusicService
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.ALBUM_ARTIST
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.ALBUM_ID
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.ALBUM_NAME
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.ARTIST_ID
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.ARTIST_NAME
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.COMPOSER
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.DATA
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.DATE_MODIFIED
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.DURATION
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.TRACK_NUMBER
import com.jooheon.clean_architecture.presentation.service.music.MusicService.Companion.YEAR
import com.jooheon.clean_architecture.presentation.service.music.extensions.*
import com.jooheon.clean_architecture.presentation.utils.MusicUtil.print

object MusicUtil {
    fun localMusicStorageUri(): Uri {
        val uri = if (VersionUtils.hasQ()) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        return uri
    }

    // song.uri 사용하자.
    fun getSongFileUri(songId: Long): Uri {
        return ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            songId
        )
    }

    // song.albumArtUri 사용하자.
    @JvmStatic
    fun getMediaStoreAlbumCoverUri(albumId: Long): Uri {
        val sArtworkUri = "content://media/external/audio/albumart".toUri()
        return ContentUris.withAppendedId(sArtworkUri, albumId)
    }

    fun parseMediaItemFromSong(song: Entity.Song) = MediaBrowserCompat.MediaItem(
        MediaDescriptionCompat.Builder().apply {
            setMediaUri(song.uri)
            setTitle(song.title)
            setSubtitle(song.artistName)
            setMediaId(song.id.toString())
            setIconUri(song.albumArtUri)
            setExtras(
                bundleOf(
                    DURATION to song.duration,
                    TRACK_NUMBER to song.trackNumber,
                    YEAR to song.year,
                    DATA to song.data,
                    DATE_MODIFIED to song.dateModified,
                    ALBUM_ID to song.albumId,
                    ALBUM_NAME to song.albumName,
                    ALBUM_ARTIST to song.albumArtist,
                    ARTIST_ID to song.artistId,
                    ARTIST_NAME to song.artistName,
                    COMPOSER to song.composer
                )
            )
        }.build(),
        MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
    )
    fun parseMetadataCompatFromSong(song: Entity.Song): MediaMetadataCompat {
        val mediaMetadataCompat = MediaMetadataCompat.Builder().apply {
            id = song.id.toString()
            title = song.title
            trackNumber = song.trackNumber.toLong()
            year = song.year.toLong()
            duration = song.duration
            data = song.data
            date = song.dateModified.toString()
            albumArtUri = song.albumArtUri.toString()

//            putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
//            putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
//            putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, song.trackNumber.toLong())
//            putLong(MediaMetadataCompat.METADATA_KEY_YEAR, song.year.toLong())
//            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
//            // data
//            putString(MediaMetadataCompat.METADATA_KEY_DATE, song.dateModified.toString())
//            putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.albumId.toString())
//            putString(MediaMetadataCompat.METADATA_KEY_WRITER, song.albumName) // albumName
//
//            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artistId.toString())
//            putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, song.albumName) // artistName
//
//            putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, song.composer)
//            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.albumArtist)
//
//            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, song.albumArtUri.toString())
//            putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.albumArtUri.toString())
        }.build()
//        mediaMetadataCompat.print()
        return mediaMetadataCompat
    }

    fun MediaMetadataCompat.print() {
        val res = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_TITLE) + ", " +
                getLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER) + ", " +
                getLong(MediaMetadataCompat.METADATA_KEY_YEAR) + ", " +
                getLong(MediaMetadataCompat.METADATA_KEY_DURATION) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_DATE) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_ALBUM) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_WRITER) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_ARTIST) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_AUTHOR) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_COMPOSER) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) + ", " +
                getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)
        Log.d("JH", description.toString())

    }
    fun MediaMetadata.print() {
        val res = getString(MediaMetadata.METADATA_KEY_MEDIA_ID) + ", " +
                getString(MediaMetadata.METADATA_KEY_TITLE) + ", " +
                getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER) + ", " +
                getLong(MediaMetadata.METADATA_KEY_YEAR) + ", " +
                getLong(MediaMetadata.METADATA_KEY_DURATION) + ", " +
                getString(MediaMetadata.METADATA_KEY_DATE) + ", " +
                getString(MediaMetadata.METADATA_KEY_ALBUM) + ", " +
                getString(MediaMetadata.METADATA_KEY_WRITER) + ", " +
                getString(MediaMetadata.METADATA_KEY_ARTIST) + ", " +
                getString(MediaMetadata.METADATA_KEY_AUTHOR) + ", " +
                getString(MediaMetadata.METADATA_KEY_COMPOSER) + ", " +
                getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) + ", " +
                getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI) + ", " +
                getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)
        Log.d("JH", res)
//        Log.d("JH", "year: ${getLong(MediaMetadata.METADATA_KEY_YEAR)}")
    }

}