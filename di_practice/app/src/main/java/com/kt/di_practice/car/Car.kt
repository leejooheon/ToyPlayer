package com.kt.di_practice.car

import android.util.Log
import com.kt.di_practice.car.parts.Common
import com.kt.di_practice.car.parts.engine.Engine
import com.kt.di_practice.car.parts.Remote
import com.kt.di_practice.car.parts.Wheels
import com.kt.di_practice.di.annotation.PerActivity
import com.kt.di_practice.driver.Driver
import javax.inject.Inject

@PerActivity
class Car @Inject constructor(
    private val engine: Engine,
    private val wheels: Wheels,
    private val driver: Driver) {

    init {
        Log.d(Common.TAG, "Car init: " + this)
    }

    @Inject
    fun connectRemote(remote: Remote) {
        // 객체가 생성된 후 무조건 호출되는듯??
        remote.setRemote()
    }

    fun drive() {
        Log.d(Common.TAG, "driving... ($this)")
        Log.d(Common.TAG, "Car driver is $driver")
        Log.d(Common.TAG, "Car wheel is $wheels")
        Log.d(Common.TAG, "Car engine is $engine")
    }
}