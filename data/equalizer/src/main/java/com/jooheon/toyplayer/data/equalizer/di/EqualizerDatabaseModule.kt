package com.jooheon.toyplayer.data.equalizer.di

import android.app.Application
import androidx.room.Room
import com.jooheon.toyplayer.data.equalizer.dao.EqualizerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EqualizerDatabaseModule {
    @Provides
    @Singleton
    fun provideEqualizerDatabase(application: Application): EqualizerDatabase =
        Room.databaseBuilder(
            application,
            EqualizerDatabase::class.java,
            "equalizer_db"
        ).build()

    @Provides
    @Singleton
    fun provideEqualizerDao(database: EqualizerDatabase) = database.dao
}