package com.jooheon.clean_architecture.toyproject.di.module

import androidx.room.Room
import com.jooheon.clean_architecture.data.api.GithubApi
import com.jooheon.clean_architecture.data.api.WikipediaApi
import com.jooheon.clean_architecture.data.dao.parkingspot.ParkingSpotDatabase
import com.jooheon.clean_architecture.toyproject.di.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataServiceModule {

    @Provides
    @Singleton
    fun providesGithubApi(@Named(Constants.GITHUB_RETROFIT) retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

    @Provides
    @Singleton
    fun providesWikipediaApi(@Named(Constants.WIKI_RETROFIT) retrofit: Retrofit): WikipediaApi = retrofit.create(WikipediaApi::class.java)

    @Provides
    @Singleton
    fun provideParkingSpotDatabase(myApplication: MyApplication) = Room.databaseBuilder(myApplication, ParkingSpotDatabase::class.java, Constants.PARKING_SPOT_DB).build()

    @Provides
    @Singleton
    fun provideParkingSpotDao(parkingSpotDatabase: ParkingSpotDatabase) = parkingSpotDatabase.dao
}