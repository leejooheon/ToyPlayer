package com.kt.di_practice.car.parts.engine

import android.util.Log
import com.kt.di_practice.car.parts.Common
import javax.inject.Inject

class DieselEngine @Inject constructor(): Engine {

    @Inject
    override fun start() {
        Log.d(Common.TAG, "DieselEngine start")
    }
}