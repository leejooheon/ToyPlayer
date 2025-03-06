package com.jooheon.toyplayer.data.music.etc

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.jooheon.toyplayer.data.music.LocalMusicDataSource.Companion.DATA
import com.jooheon.toyplayer.data.music.LocalMusicDataSource.Companion.IS_MUSIC
import com.jooheon.toyplayer.data.music.LocalMusicDataSource.Companion.baseProjection
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import java.io.File

class CursorHelper {
    internal fun getSongFromCursor(cursor: Cursor): Song {
        val audioId = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
        val title = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.TITLE)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val albumName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST)
        val data = cursor.getStringOrNull("_data")

        val audioUriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val artworkUri = Uri.parse("content://media/external/audio/albumart")

        return Song(
            audioId = audioId,
            useCache = false,
            displayName = "".defaultEmpty(),
            title = title.defaultEmpty(),
            artist = artistName.defaultEmpty(),
            artistId = artistId.toString(),
            album = albumName.defaultEmpty(),
            albumId = albumId.toString(),
            duration = duration,
            path = ContentUris.withAppendedId(audioUriExternal, audioId).toString(),
            trackNumber = trackNumber % 1000,
            imageUrl = ContentUris.withAppendedId(artworkUri, albumId).toString(),
            isFavorite = false,
            data = data
        )
    }

    internal fun makeSongCursor(context: Context, uri: Uri): Cursor? {
        val sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        val selectionFinal = "$IS_MUSIC AND $DATA LIKE ?"
        val selectionValuesFinal = addSelectionValues(
            emptyArray(), arrayListOf(
                getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).canonicalPath
            )
        )

        try {
            val res = context.contentResolver.query(
                uri,
                baseProjection,
                selectionFinal,
                selectionValuesFinal,
                sortOrder
            )
            return res
        } catch (ex: SecurityException) {
            return null
        }
    }

    private fun addSelectionValues(
        selectionValues: Array<String>?,
        paths: ArrayList<String>
    ): Array<String> {
        val selectionValuesFinal = selectionValues ?: emptyArray()

        val newSelectionValues = Array(selectionValuesFinal.size + paths.size) {
            "n = $it"
        }
        System.arraycopy(selectionValuesFinal, 0, newSelectionValues, 0, selectionValuesFinal.size)
        for (i in selectionValuesFinal.size until newSelectionValues.size) {
            newSelectionValues[i] = paths[i - selectionValuesFinal.size] + "%"
        }
        return newSelectionValues
    }
    private fun getExternalStoragePublicDirectory(type: String): File {
        return Environment.getExternalStoragePublicDirectory(type)
    }
}