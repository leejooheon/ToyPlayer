package com.jooheon.toyplayer.features.artist.di

import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.features.artist.navigation.ArtistEntryProviderInstaller
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
    fun provideMainEntryProviderInstaller(navigator: Navigator): EntryProviderInstaller =
        ArtistEntryProviderInstaller(navigator)
}