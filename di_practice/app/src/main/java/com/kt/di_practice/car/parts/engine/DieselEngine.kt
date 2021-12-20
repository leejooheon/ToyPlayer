package com.kt.di_practice.car.parts.engine

import android.util.Log
import com.kt.di_practice.car.parts.Common
import javax.inject.Inject
import javax.inject.Named

class DieselEngine @Inject constructor(
    @Named("horse_power")
    private val horsePower: Int): Engine {

    @Inject
    override fun start() {
        Log.d(Common.TAG, "DieselEngine start, horsePower: $horsePower")
    }
}