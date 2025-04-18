package com.jooheon.toyplayer.data.api.di

import android.content.Context
import com.jooheon.toyplayer.data.api.fake.ApiAssetsEqualizerService
import com.jooheon.toyplayer.data.api.fake.ApiAssetsStationsService
import com.jooheon.toyplayer.data.api.service.ApiEqualizerService
import com.jooheon.toyplayer.data.api.service.ApiKbsService
import com.jooheon.toyplayer.data.api.service.ApiMbcService
import com.jooheon.toyplayer.data.api.service.ApiSbsService
import com.jooheon.toyplayer.data.api.service.ApiStationsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
internal object ApiServiceModule {
    @Provides
    @Singleton
    fun provideApiKbsService(
        @RetrofitQualifier.KbsServer retrofit: Retrofit
    ): ApiKbsService = retrofit.create(ApiKbsService::class.java)

    @Provides
    @Singleton
    fun provideApiSbsService(
        @RetrofitQualifier.SbsServer retrofit: Retrofit
    ): ApiSbsService = retrofit.create(ApiSbsService::class.java)

    @Provides
    @Singleton
    fun provideApiMbcService(
        @RetrofitQualifier.MbcServer retrofit: Retrofit
    ): ApiMbcService = retrofit.create(ApiMbcService::class.java)

    @Provides
    @Singleton
    fun provideAssetsApi(
        @ApplicationContext context: Context,
        json: Json,
    ): ApiStationsService = ApiAssetsStationsService(
        json = json,
        kbs = context.assets.open("kbs_stations.json"),
        mbc = context.assets.open("mbc_stations.json"),
        sbs = context.assets.open("sbs_stations.json"),
        etc = context.assets.open("etc_stations.json"),
        stream = context.assets.open("stream_stations.json"),
    )

    @Provides
    @Singleton
    fun provideEqualizerApi(
        @ApplicationContext context: Context,
        json: Json,
    ): ApiEqualizerService = ApiAssetsEqualizerService(
        json = json,
        band03Equalizer = context.assets.open("equalizer_03_band_presets.json"),
        band05Equalizer = context.assets.open("equalizer_05_band_presets.json"),
        band10Equalizer = context.assets.open("equalizer_10_band_presets.json"),
        band15Equalizer = context.assets.open("equalizer_15_band_presets.json"),
        band31Equalizer = context.assets.open("equalizer_31_band_presets.json"),
    )
}