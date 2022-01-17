package com.jooheon.clean_architecture.presentation.view.splash

import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor():
    BaseViewModel() {

        fun isFirstLaunched(): Boolean {
            return true
        }
}