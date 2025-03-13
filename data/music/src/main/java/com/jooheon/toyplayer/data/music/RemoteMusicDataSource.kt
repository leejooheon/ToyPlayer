package com.jooheon.toyplayer.data.music

import com.jooheon.toyplayer.data.api.service.ApiKbsService
import com.jooheon.toyplayer.data.api.service.ApiMbcService
import com.jooheon.toyplayer.data.api.service.ApiSbsService
import com.jooheon.toyplayer.data.music.etc.TestStreamUrl
import com.jooheon.toyplayer.data.music.etc.etcStations
import com.jooheon.toyplayer.data.music.etc.kbsStations
import com.jooheon.toyplayer.data.music.etc.mbcStations
import com.jooheon.toyplayer.data.music.etc.sbsStations
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.MusicDataError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import com.jooheon.toyplayer.domain.model.radio.RadioType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

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

    fun getRadioStationList(): List<Song> {
        return  kbsStations.mapIndexed { index, radioData -> radioData.toSong(index) } +
                sbsStations.mapIndexed { index, radioData -> radioData.toSong(index) } +
                mbcStations.mapIndexed { index, radioData -> radioData.toSong(index) } +
                etcStations.mapIndexed { index, radioData -> radioData.toSong(index) }
    }

    suspend fun getKbsRadioUrl(radioData: RadioData): Result<String, MusicDataError> {
        return try {
            val response = apiKbsService.getStreamUrl(code = radioData.channelCode)
            val url = response.parseRadioUrl()

            if(url.isNullOrBlank()) Result.Error(MusicDataError.Empty)
            else Result.Success(url)
        } catch (e: Exception) {
            Result.Error(MusicDataError.Remote(cause = e.message.defaultEmpty()))
        }
    }

    suspend fun getSbsRadioUrl(radioData: RadioData): Result<String, MusicDataError> {
        return try {
            val response = apiSbsService.getStreamUrl(
                channelCode = radioData.channelCode,
                channelName = radioData.channelSubCode.defaultEmpty(),
            )

            if (response.isBlank()) Result.Error(MusicDataError.Empty)
            else Result.Success(response)
        } catch (e: Exception) {
            Result.Error(MusicDataError.Remote(cause = e.message.defaultEmpty()))
        }
    }

    suspend fun getMbcRadioUrl(radioData: RadioData): Result<String, MusicDataError> {
        return try {
            val response = apiMbcService.getStreamUrl(channel = radioData.channelCode)

            if (response.isBlank()) Result.Error(MusicDataError.Empty)
            else Result.Success(response)
        } catch (e: Exception) {
            Result.Error(MusicDataError.Remote(cause = e.message.defaultEmpty()))
        }
    }
    suspend fun getEtcRadioUrl(radioData: RadioData): Result<String, MusicDataError> {
        return if(radioData.url.isNullOrBlank()) {
            Result.Error(MusicDataError.Empty)
        } else {
            Result.Success(radioData.url!!)
        }
    }
}