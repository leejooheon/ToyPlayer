package com.kt.di_practice.di

import com.kt.di_practice.MainActivity
import com.kt.di_practice.car.Car
import dagger.Component


@Component(modules = [
    WheelsModule::class,
    DieselEngineModule::class,
])
interface CarComponent {
    fun inject(activity:MainActivity)
}