package com.jooheon.clean_architecture.presentation.view.temp

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestViewModel @Inject constructor(): BaseViewModel() {

    private val TEXT = "this is test screen"

    private val _text = mutableStateOf(TEXT)
    val text = _text

    private var testCount = 1
    fun onClicked() {
        _text.value = TEXT + testCount++
    }

    override fun onCleared() {
        super.onCleared()

        Log.d("Test", "onCleared")
    }
}