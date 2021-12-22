package com.kt.di_practice

import android.app.Application
import com.kt.di_practice.di.CarComponent
import com.kt.di_practice.di.DaggerCarComponent

class MyApplication: Application() {
    private lateinit var mComponent: CarComponent

    override fun onCreate() {
        super.onCreate()

        mComponent = DaggerCarComponent.builder()
            .airPressure(99)
            .horsePower(230)
            .build()
    }

    fun getComponent(): CarComponent {
        return mComponent
    }
}