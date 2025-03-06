package com.jooheon.toyplayer.features.main.di

import android.content.Context
import android.content.Intent
import com.jooheon.toyplayer.features.main.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ActivityModule {
    @Provides
    fun provideSingleTopActivityIntent(
        @ApplicationContext context: Context
    ): Intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}