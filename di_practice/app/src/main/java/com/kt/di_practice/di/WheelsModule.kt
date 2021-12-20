package com.kt.di_practice.di

import com.kt.di_practice.car.parts.Rims
import com.kt.di_practice.car.parts.Tires
import com.kt.di_practice.car.parts.Wheels
import dagger.Module
import dagger.Provides

@Module
class WheelsModule {
    @Provides
    fun provideRims(): Rims = Rims()

    @Provides
    fun provideTires(): Tires {
        val tires = Tires()
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