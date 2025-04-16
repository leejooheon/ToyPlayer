package com.jooheon.toyplayer.data.music

import android.content.Context
import com.jooheon.toyplayer.data.api.service.ApiKbsService
import com.jooheon.toyplayer.data.api.service.ApiMbcService
import com.jooheon.toyplayer.data.api.service.ApiSbsService
import com.jooheon.toyplayer.data.api.service.ApiStationsService
import com.jooheon.toyplayer.domain.model.common.Result
import com.jooheon.toyplayer.domain.model.common.errors.PlaybackDataError
import com.jooheon.toyplayer.domain.model.common.extension.defaultEmpty
import com.jooheon.toyplayer.domain.model.music.Song
import com.jooheon.toyplayer.domain.model.radio.RadioData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RemoteMusicDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiKbsService: ApiKbsService,
    private val apiSbsService: ApiSbsService,
    private val apiMbcService: ApiMbcService,
    private val apiStationsService: ApiStationsService,
) {
    suspend fun getStreamingMusicList(): List<Song> {
        val stations = apiStationsService.getStreamStations()
        return stations.map { it.toSong(context) }
    }

    suspend fun getRadioStationList(): List<RadioData> {
        val kbsStations = apiStationsService.getKbsStations()
        val mbcStations = apiStationsService.getMbcStations()
        val sbsStations = apiStationsService.getSbsStations()
        val etcStations = apiStationsService.getEtcStations()
        val stations = kbsStations + mbcStations + sbsStations + etcStations
        return stations.map { it.toRadioData(context) }
    }

    suspend fun getKbsRadioUrl(radioData: RadioData): Result<String, PlaybackDataError> {
        return try {
            val response = apiKbsService.getStreamUrl(code = radioData.channelCode)
            val url = response.parseRadioUrl()

            if(url.isNullOrBlank()) Result.Error(PlaybackDataError.Empty)
            else Result.Success(url)
        } catch (e: Exception) {
            Result.Error(PlaybackDataError.Remote(cause = e.message.defaultEmpty()))
        }
    }

    suspend fun getSbsRadioUrl(radioData: RadioData): Result<String, PlaybackDataError> {
        return try {
            val response = apiSbsService.getStreamUrl(
                channelCode = radioData.channelCode,
                channelName = radioData.channelSubCode.defaultEmpty(),
            )

            if (response.isBlank()) Result.Error(PlaybackDataError.Empty)
            else Result.Success(response)
        } catch (e: Exception) {
            Result.Error(PlaybackDataError.Remote(cause = e.message.defaultEmpty()))
        }
    }

    suspend fun getMbcRadioUrl(radioData: RadioData): Result<String, PlaybackDataError> {
        return try {
            val response = apiMbcService.getStreamUrl(channel = radioData.channelCode)

            if (response.isBlank()) Result.Error(PlaybackDataError.Empty)
            else Result.Success(response)
        } catch (e: Exception) {
            Result.Error(PlaybackDataError.Remote(cause = e.message.defaultEmpty()))
        }
    }

    fun getEtcRadioUrl(radioData: RadioData): Result<String, PlaybackDataError> {
        return if(radioData.url.isNullOrBlank()) {
            Result.Error(PlaybackDataError.Empty)
        } else {
            Result.Success(radioData.url!!)
        }
    }
}