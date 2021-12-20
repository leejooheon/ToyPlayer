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
    lateinit var car: Car

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val carComponent = DaggerCarComponent.builder()
            .wheelsModule(WheelsModule(59)) // 방법1: Module 생성자에 값을 넘기는 방법
            .dieselEngineModule(DieselEngineModule(230)) // 방법2: Provider를 통해 값을 넘기는 방법
            .build()

        carComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        bt_start.setOnClickListener { v ->
            car.drive()
        }
    }
}