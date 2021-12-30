package com.example.rxtest.di.component

import com.example.rxtest.di.module.app.AppModule
import com.example.rxtest.di.MyApplication
import com.example.rxtest.di.module.activity.ActivityBindingModule
import com.example.rxtest.di.module.app.ViewModelModule
import com.example.rxtest.di.module.app.NetworkModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    NetworkModule::class,
    ViewModelModule::class,
    ActivityBindingModule::class])
@Singleton
interface ApplicationComponent : AndroidInjector<MyApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyApplication>()
}