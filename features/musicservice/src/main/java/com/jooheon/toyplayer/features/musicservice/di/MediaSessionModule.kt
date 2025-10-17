package com.jooheon.toyplayer.features.musicservice.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import com.jooheon.toyplayer.domain.usecase.DefaultSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlayerSettingsUseCase
import com.jooheon.toyplayer.domain.usecase.PlaylistUseCase
import com.jooheon.toyplayer.features.common.temp.MusicServiceContext
import com.jooheon.toyplayer.features.common.temp.MusicServiceCoroutineScope
import com.jooheon.toyplayer.features.musicservice.MediaLibrarySessionCallback
import com.jooheon.toyplayer.features.musicservice.data.MediaItemProvider
import com.jooheon.toyplayer.features.musicservice.notification.GlideBitmapLoader
import com.jooheon.toyplayer.features.musicservice.usecase.CastUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlin.math.roundToInt


@Module
@InstallIn(ServiceComponent::class)
object MediaSessionModule {
    @Provides
    @ServiceScoped
    fun provideMediaLibrarySessionCallback(
        @MusicServiceContext context: Context,
        @MusicServiceCoroutineScope scope: CoroutineScope,
        mediaItemProvider: MediaItemProvider,
        playlistUseCase: PlaylistUseCase,
        playlistSettingsUseCase: PlayerSettingsUseCase,
        defaultSettingsUseCase: DefaultSettingsUseCase,
        castUseCase: CastUseCase
    ): MediaLibrarySessionCallback = MediaLibrarySessionCallback(
        context = context,
        scope = scope,
        mediaItemProvider = mediaItemProvider,
        playlistUseCase = playlistUseCase,
        playerSettingsUseCase = playlistSettingsUseCase,
        defaultSettingsUseCase = defaultSettingsUseCase,
        castUseCase = castUseCase
    )

    @OptIn(UnstableApi::class)
    @Provides
    fun provideBitmapLoader(
        @MusicServiceContext context: Context,
        @MusicServiceCoroutineScope scope: CoroutineScope,
    ): BitmapLoader = GlideBitmapLoader(
        context = context,
        scope = scope,
        bitmapSize = (256 * context.resources.displayMetrics.density).roundToInt()
    )
}