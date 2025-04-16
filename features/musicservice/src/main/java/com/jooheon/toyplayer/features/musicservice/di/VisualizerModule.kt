package com.jooheon.toyplayer.features.musicservice.di

import com.jooheon.toyplayer.domain.usecase.VisualizerObserver
import com.jooheon.toyplayer.features.musicservice.audio.VisualizerAudioProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VisualizerModule {
    @Provides
    @Singleton
    fun provideVisualizerAudioProcessor(): VisualizerAudioProcessor =
        VisualizerAudioProcessor()

    @Provides
    fun provideVisualizerObserver(
        visualizerProcessor: VisualizerAudioProcessor
    ): VisualizerObserver = visualizerProcessor
}