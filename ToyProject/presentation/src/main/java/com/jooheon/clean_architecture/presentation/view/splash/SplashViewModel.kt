package com.jooheon.clean_architecture.presentation.view.splash

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import com.jooheon.clean_architecture.presentation.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(

): BaseViewModel() {
    override val TAG: String = SplashViewModel::class.java.simpleName
    private val _done = mutableStateOf<SplashResult<*>>(SplashResult.Default)
    val done = _done

    fun prepareLaunchApp(context: Context) {
        viewModelScope.launch {
            if(!networkCheck(context).value) {
                // call dialog
            }

            if(!isFirstLaunched().value) {
                // call dialog
            }

            delay(2000L)

            _done.value = SplashResult.Done
        }
    }

    private fun networkCheck(context: Context): SplashResult.NetworkAvailable<Boolean> {
        return SplashResult.NetworkAvailable(isNetworkAvailable(context))
    }

    private fun isFirstLaunched(): SplashResult.Account<Boolean> { // FIXME
        return SplashResult.Account(false)
    }
}