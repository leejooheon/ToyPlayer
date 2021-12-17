package com.example.rxtest.di.home

import com.example.rxtest.presentation.view.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun get(): MainActivity
}