package com.kt.di_practice.car

import android.util.Log
import com.kt.di_practice.car.parts.Engine
import com.kt.di_practice.car.parts.Wheels
import javax.inject.Inject

class Car @Inject constructor(val engine: Engine, val wheels: Wheels) {
    companion object{
        private val TAG = Car::class.java.simpleName
    }

    fun drive() {
        Log.d(TAG, "driving...")
    }
}