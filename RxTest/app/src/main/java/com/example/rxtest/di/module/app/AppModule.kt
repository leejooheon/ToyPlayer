package com.example.rxtest.di.module.app

import android.content.Context
import android.content.res.Resources
import com.example.rxtest.di.MyApplication

import com.example.rxtest.di.qualifier.ApplicationContext

import dagger.Binds
import dagger.Module
import dagger.Provides

import javax.inject.Singleton


@Module
abstract class AppModule {

    @ApplicationContext
    @Binds
    abstract fun provideApplicationContext(myApplication: MyApplication): Context

    @Module
    companion object {
        @JvmStatic
        @Provides
        @Singleton
        fun provideAppResources(context: Context): Resources {
            return context.resources
        }
    }
}