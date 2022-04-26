package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val githubUseCase: GithubUseCase): BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    private val _lastSearchedOwner = mutableStateOf("")
    val lastSearchedOwner = _lastSearchedOwner

    private var _isDoubleBackPressed = mutableStateOf(true)
    val isDoubleBackPressed = _isDoubleBackPressed

    fun onNavigationClicked() {
        Log.d(TAG, "onNavigationClicked")
    }

    fun onFavoriteClicked() {
        Log.d(TAG, "onFavoriteClicked")
    }

    fun onSearchClicked() {
        Log.d(TAG, "onSearchClicked")
    }

    fun onSettingClicked() {
        Log.d(TAG, "onSettingClicked")
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _isDoubleBackPressed.value = false
            delay(2000)
            _isDoubleBackPressed.value = true
        }
    }
}