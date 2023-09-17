package com.jooheon.clean_architecture.toyproject.di.module

import android.content.Context
import android.os.Build
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jooheon.clean_architecture.data.datasource.local.AppPreferences
import com.jooheon.clean_architecture.toyproject.BuildConfig
import com.jooheon.clean_architecture.toyproject.di.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private val TAG = RetrofitModule::class.simpleName
    const val REQUEST_TIME_OUT: Long = 10

    @Provides
    @Singleton
    @Named(Constants.GITHUB_RETROFIT)
    fun providesGithubRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.GITHUB_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named(Constants.WIKI_RETROFIT)
    fun providesWikipediaRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.WIKIPEDIA_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    @Named(Constants.SUBWAY_RETROFIT)
    fun providesSubwayRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SUBWAY_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headersInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authenticator: Authenticator,
        @ApplicationContext context: Context
    ) = OkHttpClient.Builder()
        .readTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
        .connectTimeout(REQUEST_TIME_OUT, TimeUnit.SECONDS)
        .authenticator(authenticator)
        .addInterceptor(headersInterceptor)
        .addNetworkInterceptor(httpLoggingInterceptor)
        .addInterceptor(
            ChuckerInterceptor.Builder(context)
                .collector(ChuckerCollector(context))
                .maxContentLength(250000L)
                .redactHeaders(emptySet())
                .alwaysReadResponseBody(false)
                .build()
        ).build()

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls() // To allow sending null values
            .create()
    }

    @Provides
    @Singleton
    fun provideHeadersInterceptor(appPreferences: AppPreferences): Interceptor {
        val interceptor = Interceptor { chain ->
            val builder = chain.request().newBuilder();

            if (isRequireAuthorizationHeader(chain.request().url)) {
                builder.addHeader("Authorization", "Bearer ${appPreferences.firebaseToken ?: ""}")
            }
            builder.addHeader("X-Client-Desc", getXclientDescription())
            chain.proceed(builder.build())
        }

        return interceptor
    }

    @Provides
    @Singleton
    fun provideAuthenticator(): Authenticator {
        val authenticator = Authenticator { route: Route?, response: Response ->
            if (response.code != 401) {
                return@Authenticator null;
            }

            if (responseCount(response) >= 3) {
                return@Authenticator null;
            }

            return@Authenticator response.request.newBuilder().build()
        }
        return authenticator
    }

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()

    private fun getXclientDescription(): String {
        var info = BuildConfig.APPLICATION_ID

        info += "(";
        info += "Language/" + Locale.getDefault().language
        info += ";OsType/ANDROID"
        info += ";OsVer/" + System.getProperty("os.version")
        info += ";DType/" + Build.DEVICE;
        info += ";DModel/" + Build.MODEL
        info += ";Aver/" + BuildConfig.VERSION_NAME
        info += ")"

        return info
    }

    private fun isRequireAuthorizationHeader(url: HttpUrl): Boolean {
        val path = url.encodedPath
        return !path.isEmpty() &&
                (path.equals(Constants.TOKEN_URL) ||
                 path.equals(Constants.SERVICE_INFO_URL))
    }

    private fun responseCount(response: Response): Int {
        var chainingResponse = response
        var result = 1
        while (chainingResponse.priorResponse.also { chainingResponse = it!! } != null) {
            result++
        }
        return result
    }
}