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

    fun parseSongFromMediaItem(meta: MediaBrowserCompat.MediaItem): Entity.Song {
        val song = Entity.Song(
            id = meta.description.mediaId?.toLong() ?: 0,
            title = meta.description.title.toString(),
            duration = meta.description.extras?.getLong(DURATION) ?: 0,
            trackNumber = meta.description.extras?.getInt(TRACK_NUMBER) ?: 0,
            year = meta.description.extras?.getInt(YEAR) ?: 0, // here
            data = meta.description.extras?.getString(DATA) ?: "", // here,
            dateModified = meta.description.extras?.getLong(DATE_MODIFIED) ?: 0,
            albumId = meta.description.extras?.getLong(ALBUM_ID) ?: 0,
            albumName = meta.description.extras?.getString(ALBUM_NAME) ?: "",
            albumArtist = meta.description.extras?.getString(ALBUM_ARTIST) ?: "",
            artistId = meta.description.extras?.getLong(ARTIST_ID) ?: 0,
            artistName = meta.description.extras?.getString(ARTIST_NAME) ?: "",
            composer = meta.description.extras?.getString(COMPOSER) ?: ""
        )
        return song
    }

    fun parseSongFromMediaMetadata(meta: MediaMetadata): Entity.Song {
        val song = Entity.Song(
            id = meta.description.mediaId?.toLong() ?: 0,
            title = meta.description.title.toString(),
            duration = meta.getLong(MediaMetadata.METADATA_KEY_DURATION),
            trackNumber = meta.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER).toInt(),
            year = meta.getLong(MediaMetadata.METADATA_KEY_YEAR).toInt(),
            data = "null",
            dateModified = meta.getText(MediaMetadata.METADATA_KEY_DATE)?.toString()?.toLongOrNull() ?: 0,
            albumId = meta.getString(MediaMetadata.METADATA_KEY_ALBUM)?.toLongOrNull() ?: 0,
            albumName = meta.getString(MediaMetadata.METADATA_KEY_WRITER) ?: "",
            albumArtist = meta.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) ?: "",
            artistId = meta.getString(MediaMetadata.METADATA_KEY_ARTIST)?.toLongOrNull() ?: 0,
            artistName = meta.getString(MediaMetadata.METADATA_KEY_AUTHOR) ?: "",
            composer = meta.getString(MediaMetadata.METADATA_KEY_COMPOSER) ?: null
        )
        return song
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
        val mediaMetadataCompat = MediaMetadataCompat.fromMediaMetadata(
            MediaMetadata.Builder().apply {
                putString(MediaMetadata.METADATA_KEY_MEDIA_ID, song.id.toString())
                putString(MediaMetadata.METADATA_KEY_TITLE, song.title)
                putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, song.trackNumber.toLong())
                putLong(MediaMetadata.METADATA_KEY_YEAR, song.year.toLong())
                putLong(MediaMetadata.METADATA_KEY_DURATION, song.duration)
                // data
                putText(MediaMetadata.METADATA_KEY_DATE, song.dateModified.toString())
                putString(MediaMetadata.METADATA_KEY_ALBUM, song.albumId.toString())
                putString(MediaMetadata.METADATA_KEY_WRITER, song.albumName) // albumName

                putString(MediaMetadata.METADATA_KEY_ARTIST, song.artistId.toString())
                putString(MediaMetadata.METADATA_KEY_AUTHOR, song.albumName) // artistName

                putString(MediaMetadata.METADATA_KEY_COMPOSER, song.composer)
                putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, song.albumArtist)

                putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, song.albumArtUri.toString())
                putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, song.albumArtUri.toString())
            }.build()
        )
        val test = mediaMetadataCompat.mediaMetadata as MediaMetadata
        Log.d("JH", "albumId - ${test.getString(MediaMetadata.METADATA_KEY_ARTIST)}")
        Log.d("JH", "albumName - ${test.getString(MediaMetadata.METADATA_KEY_AUTHOR)}")
        return mediaMetadataCompat
    }

}