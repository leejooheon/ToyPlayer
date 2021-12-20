package com.kt.di_practice.di

import com.kt.di_practice.car.parts.engine.DieselEngine
import com.kt.di_practice.car.parts.engine.Engine
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class DieselEngineModule constructor(private val horsePower: Int) {

    @Provides
    @Named("horse_power")
    fun provideHorsePower(): Int { // 방법2: 여기에서 provide를 통해 값을 넘겨준다.
        return horsePower
    }

    @Provides
    fun provideEngine(engine: DieselEngine): Engine {
        return engine
    }
}