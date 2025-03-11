package com.jooheon.toyplayer.data.music

import android.content.Context
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.media3.common.MediaMetadata
import com.jooheon.toyplayer.core.resources.Strings
import com.jooheon.toyplayer.data.music.etc.CursorHelper
import com.jooheon.toyplayer.data.music.etc.toSong
import com.jooheon.toyplayer.domain.model.music.Song
import org.json.JSONObject
import java.io.BufferedReader
import javax.inject.Inject

class LocalMusicDataSource @Inject constructor(
    private val applicationContext: Context
) {
    fun loadFromAssets(): MutableList<Song> {
        val raw = applicationContext.assets
            .open("catalog.json")
            .bufferedReader()
            .use(BufferedReader::readText)
        val jsonObject = JSONObject(raw)
        val mediaList = jsonObject.getJSONArray("media")

        val songs = mutableListOf<Song>()
        for(i in 0 until mediaList.length()) {
            val mediaObject = mediaList.getJSONObject(i)
            if(mediaObject.getString("source").contains(".mp3")) {
                val song = mediaObject.toSong() ?: continue
                songs.add(song)
            }
        }

        return songs
    }

    fun getLocalMusicList(): MutableList<Song> {
        val cursorHelper = CursorHelper()

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val cursor = cursorHelper.makeSongCursor(applicationContext, uri)
        val songs = mutableListOf<Song>()
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val song = cursorHelper.getSongFromCursor(cursor)
                songs.add(song)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return songs
    }

    companion object {
        const val IS_MUSIC =
            MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"
        private const val ALBUM_ARTIST = "album_artist"
        const val DATA = "_data"

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