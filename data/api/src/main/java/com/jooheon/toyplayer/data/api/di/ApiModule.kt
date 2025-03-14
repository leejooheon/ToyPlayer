package com.jooheon.toyplayer.data.api.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.jooheon.toyplayer.data.api.ApiConst
import com.jooheon.toyplayer.domain.model.radio.RadioType
import com.jooheon.toyplayer.domain.model.radio.RadioType.Companion
import com.jooheon.toyplayer.domain.model.radio.RadioType.Etc
import com.jooheon.toyplayer.domain.model.radio.RadioType.Kbs
import com.jooheon.toyplayer.domain.model.radio.RadioType.Mbc
import com.jooheon.toyplayer.domain.model.radio.RadioType.Sbs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {
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
    fun provideCommonJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    @Provides
    @Singleton
    @ConverterQualifier.Json
    fun provideConverterFactory(
        json: Json
    ): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Singleton
    @ConverterQualifier.Scalars
    fun provideScalarsConverterFactory(): Converter.Factory {
        return ScalarsConverterFactory.create()
    }
}
