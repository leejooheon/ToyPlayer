package com.kt.di_practice.car.parts

import android.util.Log
import javax.inject.Inject

class Remote @Inject constructor() {
    companion object{
        private val TAG = Remote::class.java.simpleName
    }

    fun setRemote() {
        Log.d(TAG, "Remote Connected")
    }
}