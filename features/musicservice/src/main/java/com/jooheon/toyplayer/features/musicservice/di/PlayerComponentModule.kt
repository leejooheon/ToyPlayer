package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import android.os.Handler
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.Renderer
import androidx.media3.exoplayer.audio.AudioRendererEventListener
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.MediaCodecAudioRenderer
import androidx.media3.exoplayer.mediacodec.MediaCodecAdapter
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.features.musicservice.equalizer.EqualizerAudioProcessor
import com.jooheon.toyplayer.features.musicservice.playback.HlsPlaybackUriResolver
import com.jooheon.toyplayer.features.musicservice.playback.PlaybackCacheManager
import com.jooheon.toyplayer.features.musicservice.playback.factory.CustomMediaSourceFactory
import com.jooheon.toyplayer.features.musicservice.player.ToyPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope

@OptIn(UnstableApi::class)
@Module
@InstallIn(ServiceComponent::class)
object PlayerComponentModule {
    @Provides
    @ServiceScoped
    fun provideToyPlayer(player: Player): ToyPlayer = ToyPlayer(player)

    @Provides
    fun provideExoPlayer(
        @MusicServiceContext context: Context,
        mediaSourceFactory: CustomMediaSourceFactory,
        renderersFactory: DefaultRenderersFactory,
    ): Player {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setRenderersFactory(renderersFactory)
            .setAudioAttributes(audioAttributes, true) // AudioFocus가 변경될때
            .setHandleAudioBecomingNoisy(true) // 재생 주체가 변경될때 정지 (해드폰 -> 스피커)
            .build()
    }

    @OptIn(UnstableApi::class)
    @Provides
    fun provideDefaultMediaSourceFactory(
        @ApplicationContext context: Context,
        hlsPlaybackUriResolver: HlsPlaybackUriResolver,
        playbackCacheManager: PlaybackCacheManager,
    ): CustomMediaSourceFactory {
        val hlsMediaSource = ResolvingDataSource.Factory(
            DefaultDataSource.Factory(context),
            hlsPlaybackUriResolver
        )

        return CustomMediaSourceFactory(
            defaultDataSource = playbackCacheManager.cacheDataSource(),
            hlsDataSource = hlsMediaSource
        )
    }

    @Provides
    fun provideRendererFactory(
        @MusicServiceContext context: Context,
        eqProcessor: BaseAudioProcessor
    ): DefaultRenderersFactory = object : DefaultRenderersFactory(context) {
        override fun buildAudioRenderers(
            context: Context,
            extensionRendererMode: Int,
            mediaCodecSelector: MediaCodecSelector,
            enableDecoderFallback: Boolean,
            audioSink: AudioSink,
            eventHandler: Handler,
            eventListener: AudioRendererEventListener,
            out: ArrayList<Renderer>
        ) {
            out.add(
                MediaCodecAudioRenderer(
                    context,
                    MediaCodecAdapter.Factory.getDefault(context),
                    mediaCodecSelector,
                    enableDecoderFallback,
                    eventHandler,
                    eventListener,
                    DefaultAudioSink.Builder(context)
                        .setAudioProcessors(arrayOf(eqProcessor))
                        .build()
                )
            )

            super.buildAudioRenderers(
                context,
                extensionRendererMode,
                mediaCodecSelector,
                enableDecoderFallback,
                audioSink,
                eventHandler,
                eventListener,
                out
            )
        }
    }

    @Provides
    @ServiceScoped
    fun provideMultiBandParametricEq(
        @MusicServiceCoroutineScope scope: CoroutineScope,
        playerSettingsUseCase: PlayerSettingsUseCase,
    ): BaseAudioProcessor {
        return EqualizerAudioProcessor(
            scope = scope,
            playerSettingsUseCase = playerSettingsUseCase,
        )
    }
}