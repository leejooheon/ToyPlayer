package com.jooheon.toyplayer.data.api.di

import com.jooheon.toyplayer.data.api.ApiConst
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    @Provides
    @Singleton
    @RetrofitQualifier.KbsServer
    fun provideKbsServiceRetrofit(
        okHttpClient: OkHttpClient,
        @ConverterQualifier.Json converterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConst.KBS_API_SERVER)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @RetrofitQualifier.SbsServer
    fun provideSbsServiceRetrofit(
        okHttpClient: OkHttpClient,
        @ConverterQualifier.Scalars converterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConst.SBS_API_SERVER)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @RetrofitQualifier.MbcServer
    fun provideMbcServiceRetrofit(
        okHttpClient: OkHttpClient,
        @ConverterQualifier.Scalars converterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConst.MBC_API_SERVER)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
    }
}