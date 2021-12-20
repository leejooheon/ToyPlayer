package com.kt.di_practice.car.parts

import android.util.Log
import javax.inject.Inject

class Wheels constructor( // WheelsModule에 생성자가 있음!!
    private val rim: Rims,
    private val tires: Tires) {

    fun made() {
        Log.d(Common.TAG, "made wheels")
    }
}