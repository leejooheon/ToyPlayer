package com.kt.di_practice.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kt.di_practice.MyApplication
import com.kt.di_practice.R
import com.kt.di_practice.car.Car
import com.kt.di_practice.di.component.DaggerActivityComponent
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

        val component = DaggerActivityComponent.builder()
            .appComponent((application as MyApplication).getComponent())
            .airPressure(99)
            .horsePower(230)
            .build()

        component.inject(this)
    }

    override fun onResume() {
        super.onResume()
        bt_start.setOnClickListener { v ->
            car_1.drive()
            car_2.drive()
        }
        bt_second.setOnClickListener{ v->
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}