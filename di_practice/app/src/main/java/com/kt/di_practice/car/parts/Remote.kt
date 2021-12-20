package com.kt.di_practice.car.parts

import android.util.Log
import javax.inject.Inject

class Remote @Inject constructor() {

    fun setRemote() {
        Log.d(Common.TAG, "Remote Connected")
    }
}