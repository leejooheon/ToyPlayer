package com.jooheon.clean_architecture.data.datasource.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import com.jooheon.clean_architecture.domain.common.extension.defaultEmpty
import com.jooheon.clean_architecture.domain.entity.music.Song
import java.io.File
import javax.inject.Inject

class LocalMusicPlayListDataSource @Inject constructor(
    private val applicationContext: Context
): BaseLocalDataSource {

    fun getLocalSongList(uri: Uri): MutableList<Song> {
        val cursor = makeSongCursor(applicationContext, uri)
        val songs = mutableListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursor(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    private fun getSongFromCursor(cursor: Cursor): Song {
        val audioId = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
//        val displayName = cursor.getStringOrNull(MediaStore.Audio.Media.DISPLAY_NAME)
        val title = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.TITLE)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK)
        val year = cursor.getInt(MediaStore.Audio.AudioColumns.YEAR)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val dateModified = cursor.getLong(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val albumName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST)
        val composer = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")

        val audioUriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val artworkUri = Uri.parse("content://media/external/audio/albumart")

        return Song(
            audioId = audioId,
            displayName = "".defaultEmpty(),
            title = title.defaultEmpty(),
            artist = artistName.defaultEmpty(),
            artistId = artistId.toString(),
            album = albumName.defaultEmpty(),
            albumId = albumId.toString(),
            duration = duration,
            path = ContentUris.withAppendedId(audioUriExternal, audioId).toString(),
            imageUrl = ContentUris.withAppendedId(artworkUri, albumId).toString(),
            isFavorite = false
        )
    }

    private fun addSelectionValues(
        selectionValues: Array<String>,
        paths: ArrayList<String>
    ): Array<String> {
        var selectionValuesFinal = selectionValues
        if (selectionValuesFinal == null) {
            selectionValuesFinal = emptyArray()
        }
        val newSelectionValues = Array(selectionValuesFinal.size + paths.size) {
            "n = $it"
        }
        System.arraycopy(selectionValuesFinal, 0, newSelectionValues, 0, selectionValuesFinal.size)
        for (i in selectionValuesFinal.size until newSelectionValues.size) {
            newSelectionValues[i] = paths[i - selectionValuesFinal.size] + "%"
        }
        return newSelectionValues
    }

    private fun makeSongCursor(context: Context, uri: Uri): Cursor? {
        val sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
        val selectionFinal = IS_MUSIC + " AND " + DATA + " LIKE ?"
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

    @Suppress("Deprecation")
    private fun getExternalStoragePublicDirectory(type: String): File {
        return Environment.getExternalStoragePublicDirectory(type)
    }

    companion object {
        const val IS_MUSIC =
            MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"
        const val ALBUM_ARTIST = "album_artist"
        const val DATA = "_data"

        @Suppress("Deprecation")
        val baseProjection = arrayOf(
            BaseColumns._ID, // 0
            MediaStore.Audio.AudioColumns.TITLE, // 1
            MediaStore.Audio.AudioColumns.TRACK, // 2
            MediaStore.Audio.AudioColumns.YEAR, // 3
            MediaStore.Audio.AudioColumns.DURATION, // 4
            DATA, // 5
            MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 6
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
            MediaStore.Audio.AudioColumns.ALBUM, // 8
            MediaStore.Audio.AudioColumns.ARTIST_ID, // 9
            MediaStore.Audio.AudioColumns.ARTIST, // 10
            MediaStore.Audio.AudioColumns.COMPOSER, // 11
            ALBUM_ARTIST // 12
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        try {
            return getString(getColumnIndexOrThrow(columnName))
        } catch (ex: Throwable) {
            throw IllegalStateException("invalid column $columnName", ex)
        }
    }
    private fun Cursor.getLong(columnName: String): Long {
        try {
            return getLong(getColumnIndexOrThrow(columnName))
        } catch (ex: Throwable) {
            throw IllegalStateException("invalid column $columnName", ex)
        }
    }
    private fun Cursor.getInt(columnName: String): Int {
        try {
            return getInt(getColumnIndexOrThrow(columnName))
        } catch (ex: Throwable) {
            throw IllegalStateException("invalid column $columnName", ex)
        }
    }
}