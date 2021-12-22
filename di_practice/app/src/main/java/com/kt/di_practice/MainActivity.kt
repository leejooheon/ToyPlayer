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

        val carComponent = DaggerCarComponent.builder()
            .horsePower(230)
            // BindsInstance를 활용하여 생성자를 없애고, Engine에 provide로 값을 주입한다.
            // => CarComponent -> DieselEngineModule -> DieselEngine 생성
            .airPressure(55)
            // BindsInstance를 활용하여 생성자를 없애고, Tire 생성자에 값을 주입한다.
            // => CarComponent -> WheelsModule -> 객체 생성할때 값 전달 -> Tire 생성
            .build()

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