package com.jooheon.clean_architecture.data.datasource.remote

import android.content.Context
import com.jooheon.clean_architecture.data.datasource.BaseRemoteDataSource
import com.jooheon.clean_architecture.domain.entity.music.Song
import com.jooheon.clean_architecture.domain.entity.test.TestStreamUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteMusicDataSource @Inject constructor(
    private val applicationContext: Context,
): BaseRemoteDataSource() {

    suspend fun getStreamingUrlList(): List<Song> {
        val list = withContext(Dispatchers.IO) {
            delay(1000)
            TestStreamUrl.list
        }

        return list
    }
}