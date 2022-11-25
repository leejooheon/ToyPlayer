package com.jooheon.clean_architecture.toyproject.di.module

import android.content.Context
import com.jooheon.clean_architecture.presentation.service.music.MusicPlayerRemote
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
    fun provideMusicPlayerRemote(@ApplicationContext context: Context) = MusicPlayerRemote(context)

}