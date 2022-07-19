package com.jooheon.clean_architecture.data.datasource.local

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import com.jooheon.clean_architecture.data.dao.getInt
import com.jooheon.clean_architecture.data.dao.getLong
import com.jooheon.clean_architecture.data.dao.getString
import com.jooheon.clean_architecture.data.dao.getStringOrNull
import com.jooheon.clean_architecture.domain.entity.Entity
import java.io.File
import javax.inject.Inject

class MusicDataSource @Inject constructor(
    private val applicationContext: Context
): BaseLocalDataSource {

    fun getAlbums(uri: Uri): List<Entity.Song> {
        val emptySong = Entity.Song.emptySong
        return listOf(emptySong)
    }

    fun getSongs(uri: Uri): List<Entity.Song> {
        val cursor = makeSongCursor(applicationContext, uri)
        val songs = arrayListOf<Entity.Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                songs.add(getSongFromCursorImpl(cursor))
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    private fun getSongFromCursorImpl(cursor: Cursor): Entity.Song {
        val id = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
        val title = cursor.getString(MediaStore.Audio.AudioColumns.TITLE)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK)
        val year = cursor.getInt(MediaStore.Audio.AudioColumns.YEAR)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val data = cursor.getString(DATA)
        val dateModified = cursor.getLong(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val albumName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST)
        val composer = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.COMPOSER)
        val albumArtist = cursor.getStringOrNull("album_artist")
        return Entity.Song(
            id,
            title,
            trackNumber,
            year,
            duration,
            data,
            dateModified,
            albumId,
            albumName ?: "",
            artistId,
            artistName ?: "",
            composer ?: "",
            albumArtist ?: ""
        )
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
}