package com.kt.di_practice.di

import com.kt.di_practice.car.Car
import dagger.Component


@Component
interface CarComponent {
//    val Car = getCar()
    val car: Car
}