package com.jooheon.clean_architecture.presentation.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.jooheon.clean_architecture.presentation.base.extensions.albumArtUri
import com.jooheon.clean_architecture.presentation.service.music.extensions.*
import java.util.*

object MusicUtil {
    fun localMusicStorageUri(): Uri {
        val uri = if (VersionUtils.hasQ()) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        return uri
    }

    fun toReadableDurationString(songDurationMillis: Long): String {
        var minutes = songDurationMillis / 1000 / 60
        val seconds = songDurationMillis / 1000 % 60
        return if (minutes < 60) {
            String.format(
                Locale.getDefault(),
                "%02d:%02d",
                minutes,
                seconds
            )
        } else {
            val hours = minutes / 60
            minutes %= 60
            String.format(
                Locale.getDefault(),
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            )
        }
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

    fun parseMediaMetadataCompatFromMusicState(state: MusicState) = MediaMetadataCompat.Builder().apply {
        title = state.currentPlayingMusic.title
        album = state.currentPlayingMusic.albumName
        albumArtUri = state.currentPlayingMusic.albumArtUri.toString()
        artist = state.currentPlayingMusic.artistName
        duration = state.currentPlayingMusic.duration
    }.build()

}