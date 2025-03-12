package com.jooheon.toyplayer.data.api.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jooheon.toyplayer.data.api.ApiConst
import com.jooheon.toyplayer.data.api.service.ApiKbsService
import com.jooheon.toyplayer.data.api.service.ApiMbcService
import com.jooheon.toyplayer.data.api.service.ApiSbsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
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
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(ApiConst.REQUEST_TIME_OUT, TimeUnit.SECONDS)
        .connectTimeout(ApiConst.REQUEST_TIME_OUT, TimeUnit.SECONDS)
        .callTimeout(ApiConst.REQUEST_TIME_OUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addNetworkInterceptor(httpLoggingInterceptor)
        .addInterceptor(chuckerInterceptor)
        .build()

    @Provides
    @Singleton
    @ConverterQualifier.Json
    fun provideConverterFactory(): Converter.Factory {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Singleton
    @ConverterQualifier.Scalars
    fun provideScalarsConverterFactory(): Converter.Factory {
        return ScalarsConverterFactory.create()
    }

}