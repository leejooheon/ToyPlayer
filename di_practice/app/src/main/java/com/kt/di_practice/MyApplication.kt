package com.kt.di_practice

import android.app.Application
import com.kt.di_practice.di.component.ActivityComponent
import com.kt.di_practice.di.component.AppComponent
import com.kt.di_practice.di.component.DaggerAppComponent

class MyApplication: Application() {
    private lateinit var mComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        mComponent = DaggerAppComponent.create()
    }

    fun getComponent(): AppComponent {
        return mComponent
    }
}