package com.kt.di_practice.di

import com.kt.di_practice.car.parts.Rims
import com.kt.di_practice.car.parts.Tires
import com.kt.di_practice.car.parts.Wheels
import dagger.Module
import dagger.Provides

@Module
class WheelsModule constructor(private val airPressure: Int) {

    @Provides
    fun provideRims(): Rims = Rims()

    @Provides
    fun provideTires(): Tires { // 방법1: 여기에서 생성자에 값을 넘겨준다.
        val tires = Tires(airPressure)
        tires.fillAir()
        return tires
    }

    @Provides
    fun provideWheels(rims: Rims, tires: Tires): Wheels{
        val wheels = Wheels(rims, tires)
        wheels.made()
        return wheels
    }
}