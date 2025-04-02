package com.jooheon.toyplayer.data.music

import com.jooheon.toyplayer.data.api.response.PresetsResponse
import com.jooheon.toyplayer.data.api.service.ApiEqualizerService
import com.jooheon.toyplayer.domain.model.music.EqualizerType
import com.jooheon.toyplayer.domain.model.music.Preset
import javax.inject.Inject

class RemoteEqualizerDataSource @Inject constructor(
    private val apiEqualizerService: ApiEqualizerService,
) {
    suspend fun getBand03EqualizerPresets(): List<Preset> {
        val response = apiEqualizerService.getBand05EqualizerPresets()
        return response.map { it.toPreset(EqualizerType.BAND_03) }
    }
    suspend fun getBand05EqualizerPresets(): List<Preset> {
        val response = apiEqualizerService.getBand05EqualizerPresets()
        return response.map { it.toPreset(EqualizerType.BAND_05) }
    }
    suspend fun getBand10EqualizerPresets(): List<Preset> {
        val response = apiEqualizerService.getBand10EqualizerPresets()
        return response.map { it.toPreset(EqualizerType.BAND_10) }
    }
    suspend fun getBand15EqualizerPresets(): List<Preset> {
        val response = apiEqualizerService.getBand15EqualizerPresets()
        return response.map { it.toPreset(EqualizerType.BAND_15) }
    }
    suspend fun getBand31EqualizerPresets(): List<Preset> {
        val response = apiEqualizerService.getBand31EqualizerPresets()
        return response.map { it.toPreset(EqualizerType.BAND_31) }
    }

    private fun PresetsResponse.toPreset(type: EqualizerType) = Preset(
        id = this.id,
        name = this.name,
        gains = this.gains,
        isCustom = false,
        type = type
    )
}