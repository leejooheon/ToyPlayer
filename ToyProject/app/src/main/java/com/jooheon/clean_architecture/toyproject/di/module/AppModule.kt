package com.jooheon.clean_architecture.toyproject.di.module

import android.content.Context
import android.content.res.Resources
import com.jooheon.clean_architecture.toyproject.di.MyApplication

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApplicationContext(myApplication: MyApplication): Context = myApplication.applicationContext

    @Provides
    fun provideAppResources(context: Context): Resources = context.resources
}