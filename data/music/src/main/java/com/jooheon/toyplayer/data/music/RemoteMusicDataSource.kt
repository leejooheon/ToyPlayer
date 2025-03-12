package com.jooheon.toyplayer.data.music

import android.net.Uri
import com.jooheon.toyplayer.data.api.service.ApiKbsService
import com.jooheon.toyplayer.data.api.service.ApiMbcService
import com.jooheon.toyplayer.data.api.service.ApiSbsService
import com.jooheon.toyplayer.data.music.etc.TestStreamUrl
import com.jooheon.toyplayer.data.music.etc.kbsStations
import com.jooheon.toyplayer.data.music.etc.radioStations
import com.jooheon.toyplayer.data.music.etc.sbsStations
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri
import com.jooheon.toyplayer.data.music.etc.mbcStations
import com.jooheon.toyplayer.domain.model.RadioRawData
import com.jooheon.toyplayer.domain.model.common.extension.defaultTrue


class RemoteMusicDataSource @Inject constructor(
    private val apiKbsService: ApiKbsService,
    private val apiSbsService: ApiSbsService,
    private val apiMbcService: ApiMbcService,
) {
    suspend fun getStreamingMusicList(): List<Song> {
        val list = withContext(Dispatchers.IO) {
            TestStreamUrl.list
        }
        return list
    }

    suspend fun getRadioStationList(): List<Song> {
//        val kbsStations = getKbsRadioList()
//        val sbsStations = getSbsRadioList()
        val mbcStations = getMbcRadioList()
        return mbcStations + radioStations
//        return kbsStations + sbsStations + mbcStations + radioStations
    }

    suspend fun getKbsRadioList(): List<Song> {
        val songs = kbsStations.mapIndexedNotNull { index, data ->
            val response = apiKbsService.getStreamUrl(
                code = data.channelCode
            )

            response.toSongOrNull(index, data)
        }

        return songs
    }

    suspend fun getSbsRadioList(): List<Song> {
        val songs = sbsStations.mapIndexedNotNull { index, data ->
            val response = apiSbsService.getStreamUrl(
                channelCode = data.channelCode,
                channelName = data.channelSubCode.defaultEmpty(),
            )
            Timber.d("getSbsRadioList: $response")
            planeTextToSongOrNull(index, response, data, "SBS")
        }
        songs.forEach { Timber.d("getSbsRadioList: ${it.title}") }
        return songs
    }

    suspend fun getMbcRadioList(): List<Song> {
        val songs = mbcStations.mapIndexedNotNull { index, data ->
            val response = apiMbcService.getStreamUrl(
                channel = data.channelCode
            )
            planeTextToSongOrNull(index, response, data, "MBC")
        }

        return songs
    }

    private fun planeTextToSongOrNull(
        index: Int,
        response: String,
        rawData: RadioRawData,
        id: String,
    ): Song? {
        val oneDayLater = System.currentTimeMillis() + (3600000 * 24)

        if(!response.toUri().path?.endsWith(".m3u8", ignoreCase = true).defaultTrue()) {
            Timber.d("planeTextToSongOrNull: ${response.toUri().path}, $response, ")
            return null
        }

        val uri = response.toUri()
            .buildUpon()
            .appendQueryParameter("Expires", oneDayLater.toString())
            .build()
            .toString()

        val sbsId = id.hashCode().toString()

        return Song(
            audioId = rawData.channelName.hashCode().toLong(),
            useCache = false,
            displayName = rawData.channelName,
            title = rawData.channelName,
            artist = rawData.channelName,
            artistId = sbsId,
            album = "$id Radio",
            albumId = sbsId,
            duration = -1,
            path = uri,
            imageUrl = "",
            trackNumber = index
        )
    }
}