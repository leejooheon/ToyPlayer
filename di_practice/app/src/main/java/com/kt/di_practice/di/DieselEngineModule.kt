package com.kt.di_practice.di

import com.kt.di_practice.car.parts.engine.DieselEngine
import com.kt.di_practice.car.parts.engine.Engine
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DieselEngineModule {
    @Singleton
    @Provides
    fun provideEngine(engine: DieselEngine): Engine {
        return engine
    }
}