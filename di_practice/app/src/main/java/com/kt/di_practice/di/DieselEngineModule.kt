package com.kt.di_practice.di

import com.kt.di_practice.car.parts.engine.DieselEngine
import com.kt.di_practice.car.parts.engine.Engine
import dagger.Module
import dagger.Provides

@Module
class DieselEngineModule { // Module constructor를 지웠다.
    @Provides
    fun provideEngine(engine: DieselEngine): Engine {
        return engine
    }
}