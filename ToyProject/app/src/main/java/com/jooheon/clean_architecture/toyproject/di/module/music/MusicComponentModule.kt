package com.jooheon.clean_architecture.toyproject.di.module.music

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.jooheon.clean_architecture.domain.repository.MusicListRepository
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.features.main.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicComponentModule {
    @Provides
    fun provideRootActivityIntent(
        @ApplicationContext context: Context
    ): Intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    @Provides
    @Singleton
    @SuppressLint("UnsafeOptInUsageError")
    fun provideExoPlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer {
        // 오디오 포커스 관리: https://developer.android.com/guide/topics/media-apps/audio-focus
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        return exoPlayer
    }

    @Provides
    @Singleton
    fun providesMusicListUseCase(
        applicationScope: CoroutineScope,
        musicListRepository: MusicListRepository,
    ): MusicListUseCase =
        MusicListUseCase(
            applicationScope = applicationScope,
            musicListRepository = musicListRepository,
        )
}