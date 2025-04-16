package com.jooheon.toyplayer.data.api.service

import com.jooheon.toyplayer.data.api.response.PresetsResponse
import retrofit2.http.GET

interface ApiEqualizerService {
    @GET("/app/main/data/api/src/main/assets/equalizer_03_band_presets.json")
    suspend fun getBand03EqualizerPresets(): List<PresetsResponse>
    @GET("/app/main/data/api/src/main/assets/equalizer_05_band_presets.json")
    suspend fun getBand05EqualizerPresets(): List<PresetsResponse>
    @GET("/app/main/data/api/src/main/assets/equalizer_10_band_presets.json")
    suspend fun getBand10EqualizerPresets(): List<PresetsResponse>
    @GET("/app/main/data/api/src/main/assets/equalizer_15_band_presets.json")
    suspend fun getBand15EqualizerPresets(): List<PresetsResponse>
    @GET("/app/main/data/api/src/main/assets/equalizer_31_band_presets.json")
    suspend fun getBand31EqualizerPresets(): List<PresetsResponse>
}