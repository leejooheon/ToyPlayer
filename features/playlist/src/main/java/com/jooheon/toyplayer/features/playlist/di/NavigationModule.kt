package com.jooheon.toyplayer.features.playlist.di

import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.features.playlist.navigation.PlaylistEntryProviderInstaller
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationModule {
    @IntoSet
    @Provides
    fun providePlaylistEntryProviderInstaller(navigator: Navigator): EntryProviderInstaller =
        PlaylistEntryProviderInstaller(navigator)
}