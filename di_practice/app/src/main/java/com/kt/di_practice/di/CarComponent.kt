package com.kt.di_practice.di

import com.kt.di_practice.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named


@Component(modules = [
    WheelsModule::class,
    DieselEngineModule::class,
])
interface CarComponent {
    fun inject(activity:MainActivity)

    @Component.Builder
    interface Builder {
        fun build(): CarComponent

        @BindsInstance
        fun horsePower(@Named("horse_power") horsePower: Int): Builder

        @BindsInstance
        fun airPressure(@Named("air_pressure") airPressure: Int): Builder
    }
}