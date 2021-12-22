package com.kt.di_practice.di.component

import com.kt.di_practice.view.MainActivity
import com.kt.di_practice.di.annotation.PerActivity
import com.kt.di_practice.di.module.DieselEngineModule
import com.kt.di_practice.di.module.WheelsModule
import com.kt.di_practice.view.SecondActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named


@PerActivity
@Component(
    modules = [WheelsModule::class, DieselEngineModule::class],
    dependencies = [AppComponent::class]
)
interface ActivityComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: SecondActivity)

    @Component.Builder
    interface Builder {
        fun build(): ActivityComponent

        fun appComponent(appComponent: AppComponent): Builder

        @BindsInstance
        fun horsePower(@Named("horse_power") horsePower: Int): Builder

        @BindsInstance
        fun airPressure(@Named("air_pressure") airPressure: Int): Builder
    }
}