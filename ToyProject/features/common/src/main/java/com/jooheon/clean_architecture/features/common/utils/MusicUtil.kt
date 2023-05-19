package com.jooheon.clean_architecture.features.common.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.webkit.URLUtil
import androidx.core.net.toUri
import com.jooheon.clean_architecture.domain.entity.music.Song
import java.util.*

object MusicUtil {
    fun localMusicStorageUri(): Uri {
        val uri = if (VersionUtil.hasQ()) {
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
    fun getSongFileUri(path: String): Uri {
        if (URLUtil.isHttpUrl(path) ||
            URLUtil.isHttpsUrl(path) ||
            URLUtil.isContentUrl(path)) {
            return path.toUri()
        } else {
            val uri = path.toLongOrNull() ?: -1L
            return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, uri)
        }

        return Uri.parse("")
    }

    // song.albumArtUri 사용하자.
    @JvmStatic
    fun getMediaStoreAlbumCoverUri(imageUrl: String): Uri {
        try {
            if (URLUtil.isHttpUrl(imageUrl) ||
                URLUtil.isHttpsUrl(imageUrl) ||
                URLUtil.isContentUrl(imageUrl)) {
                return imageUrl.toUri()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return defaultImgaeUri()
    }

    fun defaultImgaeUri(): Uri {
        return "https://upload.wikimedia.org/wikipedia/commons/2/21/Solid_black.svg".toUri()
    }
}