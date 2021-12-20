package com.kt.di_practice.car

import android.util.Log
import com.kt.di_practice.car.parts.Common
import com.kt.di_practice.car.parts.Engine
import com.kt.di_practice.car.parts.Remote
import com.kt.di_practice.car.parts.Wheels
import javax.inject.Inject

class Car @Inject constructor(
    private val engine: Engine,
    private val wheels: Wheels) {

    init {
        Log.d(Common.TAG, "Car init")
    }

    @Inject
    fun connectRemote(remote: Remote) {
        // 객체가 생성된 후 무조건 호출되는듯??
        remote.setRemote()
    }

    fun drive() {
        Log.d(Common.TAG, "driving...")
    }
}