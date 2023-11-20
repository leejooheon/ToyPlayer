package com.jooheon.clean_architecture.features.musicservice.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PlayerComponentModule {

    @Provides
    @Singleton
    @UnstableApi
    fun provideExoPlayer(
        @ApplicationContext context: Context,
    ): ExoPlayer {
        // 오디오 포커스 관리: https://developer.android.com/guide/topics/media-apps/audio-focus
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .setWakeMode(C.WAKE_MODE_NETWORK) // 잠금화면에서 Wifi를 이용한 백그라운드 재생 허용
            .build()

        return exoPlayer
    }
}