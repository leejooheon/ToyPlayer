package com.kt.di_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt.di_practice.di.CarComponent
import com.kt.di_practice.di.DaggerCarComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val carComponent = DaggerCarComponent.create();
        val car = carComponent.car

        car.drive()
    }
}