package com.jooheon.toyplayer.di.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

// 참고자료: https://medium.com/androiddevelopers/create-an-application-coroutinescope-using-hilt-dd444e721528
@Module
@InstallIn(SingletonComponent::class)
object CoroutinesScopesModule {
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}