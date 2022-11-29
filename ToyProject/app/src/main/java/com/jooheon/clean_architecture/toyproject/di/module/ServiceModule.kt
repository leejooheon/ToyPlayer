package com.jooheon.clean_architecture.toyproject.di.module

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
import com.jooheon.clean_architecture.presentation.service.music.datasource.LocalMusicPlayerDataSource
import com.jooheon.clean_architecture.presentation.service.music.datasource.MusicPlayerDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun providesAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @ServiceScoped
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setHandleAudioBecomingNoisy(true)
        .build()

    @ServiceScoped
    @Provides
    fun provideMusicPlayerRemote(@ApplicationContext context: Context) = MusicPlayerRemote(context)

    @ServiceScoped
    @Provides
    fun providesDataSourceFactor(
        @ApplicationContext context: Context
    ): DefaultDataSource.Factory = DefaultDataSource.Factory(context)

}

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceInterfaces {

    @ServiceScoped
    @Binds
    abstract fun bindsMusicDataSource(
        localMusicPlayerDataSource: LocalMusicPlayerDataSource
    ): MusicPlayerDataSource
}