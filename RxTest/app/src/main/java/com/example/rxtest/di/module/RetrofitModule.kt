package com.example.rxtest.di.module

import com.example.rxtest.BuildConfig
import com.example.rxtest.data.api.GithubApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    private val TAG = RetrofitModule::class.simpleName

    @Provides
    @Singleton
    fun providesRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideHttpClient(okhttpClientBuilder: OkHttpClient.Builder,
                          httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        if(BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okhttpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor)
        }

        return okhttpClientBuilder
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
//            .addInterceptor(createInterceptor())
//            .authenticator(createAuthenticator())
            .build()
    }

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
    fun providesGithubApi(retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

//    private fun createAuthenticator(): Authenticator {
//        val authenticator = Authenticator { route: Route?, response: Response ->
//            Log.d(TAG, ">>> authenticate: url: " + response.request.url + ", count: " + responseCount(response))
//            Log.d(TAG, ">>> authenticate: response_code: " + response.code)
//
//            if (response.code != 401) {
//                return@Authenticator null;
//            }
//
//            if (responseCount(response) >= 3) {
//                return@Authenticator null;
//            }
//
//            return@Authenticator response.request.newBuilder().build()
//        }
//        return authenticator
//    }
//
//    private fun createInterceptor(): Interceptor {
//        return Interceptor { chain: Interceptor.Chain ->
//            val original = chain.request()
//            val builder = original.newBuilder()
//            if (isRequireAuthorizationHeader(original.url)) {
//                Log.d(TAG, ">>> addHeader() - Authorization")
//            } else {
//                Log.d(TAG, ">>>createRetrofit::No Needed Header Url: \" + original.url()")
//            }
//
//            builder.addHeader("X-Client-Desc", getXclientDescription())
//            chain.proceed(builder.build())
//        }
//    }
//
//    private fun getXclientDescription(): String {
//        var info = BuildConfig.APPLICATION_ID
//
//        info += "(";
//        info += "Language/" + Locale.getDefault().language
//        info += ";OsType/ANDROID"
//        info += ";OsVer/" + System.getProperty("os.version")
//        info += ";DType/" + Build.DEVICE;
//        info += ";DModel/" + Build.MODEL
//        info += ";Aver/" + BuildConfig.VERSION_NAME
//        info += ")"
//
//        return info
//    }
//
//    private fun isRequireAuthorizationHeader(url: HttpUrl): Boolean {
//        val path = url.encodedPath
//        return !TextUtils.isEmpty(path) &&
//                path != "/oauth/token" &&
//                path != "/api/v100/service/info"
//    }
//
//    private fun responseCount(response: Response): Int {
//        var result = 0
//        var looper: Response?
//
//        do {
//            result += 1
//            looper = response.priorResponse
//        } while(looper != null)
//
//        return result
//    }

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder = OkHttpClient.Builder()

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor()
}