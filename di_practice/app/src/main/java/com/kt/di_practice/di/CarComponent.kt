package com.kt.di_practice.di

import com.kt.di_practice.MainActivity
import com.kt.di_practice.car.Car
import dagger.Component


@Component
interface CarComponent {
    fun inject(activity:MainActivity)
}