package com.jooheon.toyplayer.data.api.fake

import com.jooheon.toyplayer.data.api.response.StationResponse
import com.jooheon.toyplayer.data.api.response.StreamResponse
import com.jooheon.toyplayer.data.api.service.ApiStationsService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

@OptIn(ExperimentalSerializationApi::class)
class ApiAssetsStationsService(
    private val json: Json,
    private val kbs: InputStream,
    private val mbc: InputStream,
    private val sbs: InputStream,
    private val etc: InputStream,
    private val stream: InputStream,
): ApiStationsService {
    override suspend fun getKbsStations(): List<StationResponse> {
        return json.decodeFromStream(kbs.resetStream())
    }

    override suspend fun getMbcStations(): List<StationResponse> {
        return json.decodeFromStream(mbc.resetStream())
    }

    override suspend fun getSbsStations(): List<StationResponse> {
        return json.decodeFromStream(sbs.resetStream())
    }

    override suspend fun getEtcStations(): List<StationResponse> {
        return json.decodeFromStream(etc.resetStream())
    }

    override suspend fun getStreamStations(): List<StreamResponse> {
        return json.decodeFromStream(stream.resetStream())
    }
}