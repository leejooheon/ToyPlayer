package com.kt.di_practice.car.parts

import android.util.Log

class Tires constructor(private val airPressure: Int) { // WheelsModule에 생성자가 있음!!

    fun fillAir() {
        Log.d(Common.TAG, "filling air to tires... airPressure: $airPressure")
    }
}