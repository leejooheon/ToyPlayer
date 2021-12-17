package com.example.rxtest.di.component

import com.example.rxtest.di.AppModule
import com.example.rxtest.di.MyApplication
import com.example.rxtest.di.ViewModelModule
import com.example.rxtest.di.home.MainActivityModule
import com.example.rxtest.di.home.MainModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ViewModelModule::class,
    MainActivityModule::class,
    MainModule::class])
@Singleton
interface AppComponent : AndroidInjector<MyApplication> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyApplication>()
}