package com.jooheon.toyplayer.features.common.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.webkit.URLUtil
import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.*

object MusicUtil {
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
    fun getDateModifiedString(date: Long): String {
        val calendar: Calendar = Calendar.getInstance()
        val pattern = "yyyy/MM/dd hh:mm:ss"
        calendar.timeInMillis = date
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        return formatter.format(calendar.time)
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
    }

    // song.albumArtUri 사용하자.
    @JvmStatic
    fun getMediaStoreAlbumCoverUri(imageUrl: String): Uri {
        try {
            if (URLUtil.isHttpUrl(imageUrl) ||
                URLUtil.isHttpsUrl(imageUrl) ||
                URLUtil.isContentUrl(imageUrl) ||
                imageUrl.startsWith("android.resource://")) {
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