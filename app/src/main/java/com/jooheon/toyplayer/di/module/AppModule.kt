package com.jooheon.toyplayer.di.module

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import com.jooheon.toyplayer.features.main.MainActivity
import com.jooheon.toyplayer.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    fun provideMyApplication(application: Application): MyApplication = application as MyApplication

    @Provides
    fun provideSingleTopActivityIntent(
        @ApplicationContext context: Context
    ): Intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}