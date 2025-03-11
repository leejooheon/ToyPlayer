package com.jooheon.toyplayer.data.music

import com.jooheon.toyplayer.data.music.etc.TestStreamUrl
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class RemoteMusicDataSource @Inject constructor() {
    suspend fun getStreamingMusicList(): List<Song> {
        val list = withContext(Dispatchers.IO) {
            TestStreamUrl.list
        }

        return list
    }
}