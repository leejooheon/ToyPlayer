package com.example.rxtest.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rxtest.presentation.view.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ir.hosseinabbasi.mvvm.di.qualifier.ViewModelKey

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}