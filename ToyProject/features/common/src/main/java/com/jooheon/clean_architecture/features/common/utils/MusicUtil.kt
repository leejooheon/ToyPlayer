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
    fun getSongFileUri(audioId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioId)
    }

    // song.albumArtUri 사용하자.
    @JvmStatic
    fun getMediaStoreAlbumCoverUri(song: Song): Uri {
        val imageUrl = song.imageUrl
        if (URLUtil.isHttpUrl(imageUrl) || URLUtil.isHttpsUrl(imageUrl)) {
            return imageUrl.toUri()
        } else {
            val sArtworkUri = "content://media/external/audio/albumart".toUri()
            return ContentUris.withAppendedId(sArtworkUri, song.audioId)
        }
    }

    fun testImageUri(): Uri {
        val path = "/440/44063.jpg"
        val updDt = 1669796869000
        return (GlideUtil.TMP_ESSENTIAL_TITLE_URL + path + "?" + updDt).toUri()
    }
}