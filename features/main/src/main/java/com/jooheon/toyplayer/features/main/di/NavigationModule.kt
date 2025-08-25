package com.jooheon.toyplayer.features.main.di

import com.jooheon.toyplayer.core.navigation.EntryProviderInstaller
import com.jooheon.toyplayer.core.navigation.Navigator
import com.jooheon.toyplayer.features.main.navigation.MainEntryProviderInstaller
import com.jooheon.toyplayer.features.main.navigation.MainNavigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object NavigationModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(): Navigator = MainNavigator()

    @IntoSet
    @Provides
    fun provideMainEntryProviderInstaller(navigator: Navigator): EntryProviderInstaller =
        MainEntryProviderInstaller(navigator)
}