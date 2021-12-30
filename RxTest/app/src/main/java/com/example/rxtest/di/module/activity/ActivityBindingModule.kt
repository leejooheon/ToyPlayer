package com.example.rxtest.di.module.activity

import com.example.rxtest.di.module.activity.fragment.FragmentBindingModule
import com.example.rxtest.di.scope.PerActivity
import com.example.rxtest.presentation.view.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun mainActivity(): MainActivity
}