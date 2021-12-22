package com.kt.di_practice.di.module

import com.kt.di_practice.car.parts.engine.DieselEngine
import com.kt.di_practice.car.parts.engine.Engine
import com.kt.di_practice.di.annotation.PerActivity
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DieselEngineModule {
    @PerActivity
    @Provides
    fun provideEngine(engine: DieselEngine): Engine {
        return engine
    }
}