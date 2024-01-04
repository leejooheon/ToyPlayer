package com.jooheon.toyplayer.data.datasource.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.data.R
import com.jooheon.toyplayer.domain.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.entity.music.MediaFolder
import com.jooheon.toyplayer.domain.entity.music.MediaId
import com.jooheon.toyplayer.domain.entity.music.Song
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import javax.inject.Inject

class LocalMusicDataSource @Inject constructor(
    private val applicationContext: Context
): BaseLocalDataSource {

    fun loadFromAssets(): MutableList<Song> {
        val raw = applicationContext.assets
            .open("catalog.json")
            .bufferedReader()
            .use(BufferedReader::readText)
        val jsonObject = JSONObject(raw)
        val mediaList = jsonObject.getJSONArray("media")

        val songs = mutableListOf<Song>()
        for(i in 0 until mediaList.length()) {
            val mediaObject =  mediaList.getJSONObject(i)
            if(mediaObject.getString("genre") == "Video") continue
            val song = getSongFromJsonObject(mediaObject) ?: continue
            songs.add(song)
        }

        return songs
    }

    fun getLocalMusicList(): MutableList<Song> {
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

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

    fun getMediaFolderList(): List<MediaFolder> {
        val allSongs = MediaFolder(
            title = applicationContext.getString(R.string.media_folder_all_songs),
            mediaId = MediaId.AllSongs,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )
        val album = MediaFolder(
            title = applicationContext.getString(R.string.media_folder_album),
            mediaId = MediaId.AlbumRoot,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )
        val playlist = MediaFolder(
            title = applicationContext.getString(R.string.media_folder_playlist),
            mediaId = MediaId.Playlist,
            mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
        )

        return listOf(allSongs, album, playlist)
    }

    private fun getSongFromJsonObject(mediaObject: JSONObject): Song? {
        try {
            val trackId = mediaObject.getString("id")
            val albumName = mediaObject.getString("album")
            val title = mediaObject.getString("title")
            val artistName = mediaObject.getString("artist")
            val trackNumber = mediaObject.getInt("trackNumber")
            val duration = (mediaObject.getInt("duration") * 1000).toLong()
            val sourceUri = mediaObject.getString("source")
            val imageUri = mediaObject.getString("image")

            return Song(
                audioId = trackId.hashCode().toLong(),
                useCache = true,
                displayName = trackId,
                title = title.defaultEmpty(),
                artist = artistName.defaultEmpty(),
                artistId = "unset",
                album = albumName.defaultEmpty(),
                albumId = "unset",
                duration = duration,
                path = sourceUri,
                trackNumber = trackNumber % 1000,
                imageUrl = imageUri,
                isFavorite = false,
            )
        } catch (e: JSONException) {
            return null
        }
    }

    private fun getSongFromCursor(cursor: Cursor): Song {
        val audioId = cursor.getLong(MediaStore.Audio.AudioColumns._ID)
        val title = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.TITLE)
        val trackNumber = cursor.getInt(MediaStore.Audio.AudioColumns.TRACK)
        val duration = cursor.getLong(MediaStore.Audio.AudioColumns.DURATION)
        val albumId = cursor.getLong(MediaStore.Audio.AudioColumns.ALBUM_ID)
        val albumName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ALBUM)
        val artistId = cursor.getLong(MediaStore.Audio.AudioColumns.ARTIST_ID)
        val artistName = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.ARTIST)
        val data = cursor.getStringOrNull("_data")
//        val displayName = cursor.getStringOrNull(MediaStore.Audio.Media.DISPLAY_NAME)
//        val year = cursor.getInt(MediaStore.Audio.AudioColumns.YEAR)
//        val dateModified = cursor.getLong(MediaStore.Audio.AudioColumns.DATE_MODIFIED)
//        val composer = cursor.getStringOrNull(MediaStore.Audio.AudioColumns.COMPOSER)
//        val albumArtist = cursor.getStringOrNull("album_artist")

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