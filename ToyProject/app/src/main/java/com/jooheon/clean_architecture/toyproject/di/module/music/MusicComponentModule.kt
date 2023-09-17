package com.jooheon.clean_architecture.toyproject.di.module.music

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.jooheon.clean_architecture.domain.repository.MusicListRepository
import com.jooheon.clean_architecture.domain.usecase.music.list.MusicListUseCase
import com.jooheon.clean_architecture.features.main.MainActivity
import com.jooheon.clean_architecture.features.musicservice.notification.PlayingMediaNotificationManager
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
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .setWakeMode(C.WAKE_MODE_NETWORK) // 잠금화면에서 Wifi를 이용한 백그라운드 재생 허용
            .build()

        return exoPlayer
    }

    @Provides
    @Singleton
    @UnstableApi
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer
    ): MediaSession {
        val trampolineIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val sessionActivityIntent = PendingIntent.getActivity(
            context,
            0,
            trampolineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val mediaSession = MediaSession.Builder(context, player).build().apply {
            setSessionActivity(sessionActivityIntent)
        }

        return mediaSession
    }

    @Provides
    fun providePlayingMediaNotificationManager(
        @ApplicationContext context: Context,
        applicationScope: CoroutineScope,
        player: ExoPlayer,
    ): PlayingMediaNotificationManager = PlayingMediaNotificationManager(
        context = context,
        applicationScope = applicationScope,
        player = player
    )

    @Provides
    fun provideRootActivityIntent(
        @ApplicationContext context: Context
    ): Intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
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