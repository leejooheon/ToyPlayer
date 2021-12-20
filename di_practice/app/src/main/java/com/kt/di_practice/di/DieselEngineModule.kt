package com.kt.di_practice.di

import com.kt.di_practice.car.parts.engine.DieselEngine
import com.kt.di_practice.car.parts.engine.Engine
import dagger.Binds
import dagger.Module

@Module
abstract class DieselEngineModule {

    // 매개변수로 Engine을 implement하는 DieselEngine 변수 하나만 들어있고 abstract 메소드로 선언되어 있다.
    @Binds
    abstract fun bindEngine(engine: DieselEngine): Engine
}