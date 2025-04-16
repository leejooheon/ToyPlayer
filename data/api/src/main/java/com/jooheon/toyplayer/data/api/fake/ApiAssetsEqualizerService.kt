package com.jooheon.toyplayer.data.api.fake

import com.jooheon.toyplayer.data.api.response.PresetsResponse
import com.jooheon.toyplayer.data.api.service.ApiEqualizerService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream

@OptIn(ExperimentalSerializationApi::class)
class ApiAssetsEqualizerService(
    private val json: Json,
    private val band03Equalizer: InputStream,
    private val band05Equalizer: InputStream,
    private val band10Equalizer: InputStream,
    private val band15Equalizer: InputStream,
    private val band31Equalizer: InputStream,
): ApiEqualizerService {
    override suspend fun getBand03EqualizerPresets(): List<PresetsResponse> {
        return json.decodeFromStream(band03Equalizer.resetStream())
    }

    override suspend fun getBand05EqualizerPresets(): List<PresetsResponse> {
        return json.decodeFromStream(band05Equalizer.resetStream())
    }
    override suspend fun getBand10EqualizerPresets(): List<PresetsResponse> {
        return json.decodeFromStream(band10Equalizer.resetStream())
    }

    override suspend fun getBand15EqualizerPresets(): List<PresetsResponse> {
        return json.decodeFromStream(band15Equalizer.resetStream())
    }

    override suspend fun getBand31EqualizerPresets(): List<PresetsResponse> {
        return json.decodeFromStream(band31Equalizer.resetStream())
    }
}