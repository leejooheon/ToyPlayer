package com.kt.di_practice.di.component

import com.kt.di_practice.driver.Driver
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {
    fun getDriver(): Driver
}