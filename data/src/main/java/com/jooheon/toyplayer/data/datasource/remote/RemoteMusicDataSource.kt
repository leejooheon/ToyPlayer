package com.jooheon.toyplayer.data.datasource.remote

import android.content.Context
import com.jooheon.toyplayer.domain.entity.music.Song
import com.jooheon.toyplayer.domain.entity.test.TestStreamUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteMusicDataSource @Inject constructor(
    private val applicationContext: Context,
): BaseRemoteDataSource() {

    suspend fun getStreamingMusicList(): List<Song> {
        val list = withContext(Dispatchers.IO) {
            delay(1000)
            TestStreamUrl.list
        }

        return list
    }
}