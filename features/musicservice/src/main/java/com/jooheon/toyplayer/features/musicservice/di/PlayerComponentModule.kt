package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import android.os.Handler
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.player.ToyPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@OptIn(UnstableApi::class)
@Module
@InstallIn(ServiceComponent::class)
object PlayerComponentModule {
    @Provides
    @ServiceScoped
    fun provideToyPlayer(
        player: Player,
        @MusicServiceCoroutineScope scope: CoroutineScope,
    ): ToyPlayer = ToyPlayer(player, scope)

    @Provides
    fun provideExoPlayer(
        @MusicServiceContext context: Context,
        mediaSourceFactory: DefaultMediaSourceFactory,
    ): Player {
        val rendererFactory = DefaultRenderersFactory(context)

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setRenderersFactory(rendererFactory)
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .build()
    }

    @OptIn(UnstableApi::class)
    @Provides
    fun provideDefaultMediaSourceFactory(
        playbackUriResolver: PlaybackUriResolver,
        playbackCacheManager: PlaybackCacheManager,
    ): DefaultMediaSourceFactory = DefaultMediaSourceFactory(
        ResolvingDataSource.Factory(
            playbackCacheManager.cacheDataSource(),
            playbackUriResolver
        ),
        DefaultExtractorsFactory()
    )
}