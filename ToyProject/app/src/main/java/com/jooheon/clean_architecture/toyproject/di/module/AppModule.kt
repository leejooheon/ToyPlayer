package com.jooheon.clean_architecture.toyproject.di.module

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.jooheon.clean_architecture.presentation.base.extensions.DiName
import com.jooheon.clean_architecture.toyproject.di.MyApplication

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApplicationContext(myApplication: MyApplication): Context = myApplication.applicationContext

    @Provides
    fun provideAppResources(context: Context): Resources = context.resources

    @Provides
    fun provideMyApplication(application: Application): MyApplication = application as MyApplication

    @Provides
    @Named(DiName.IO)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named(DiName.MAIN)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}