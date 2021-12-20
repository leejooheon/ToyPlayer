package com.kt.di_practice.di

import com.kt.di_practice.car.parts.Rims
import com.kt.di_practice.car.parts.Tires
import com.kt.di_practice.car.parts.Wheels
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class WheelsModule { // Module constructor를 지웠다.

    @Provides
    fun provideRims(): Rims = Rims()

    @Provides
    fun provideTires(@Named("air_pressure") airPressure: Int): Tires { // Component에 선언된 값이 주입된다.
        val tires = Tires(airPressure)
        tires.fillAir()
        return tires
    }

    @Provides
    fun provideWheels(rims: Rims, tires: Tires): Wheels {
        val wheels = Wheels(rims, tires)
        wheels.made()
        return wheels
    }
}