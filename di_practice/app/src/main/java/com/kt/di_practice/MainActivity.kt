package com.kt.di_practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt.di_practice.car.Car
import com.kt.di_practice.di.CarComponent
import com.kt.di_practice.di.DaggerCarComponent
import com.kt.di_practice.di.DieselEngineModule
import com.kt.di_practice.di.WheelsModule
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var car_1: Car
    @Inject
    lateinit var car_2: Car

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val carComponent = (application as MyApplication).getComponent()

        carComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        bt_start.setOnClickListener { v ->
            car_1.drive()
            car_2.drive()
        }
    }
}