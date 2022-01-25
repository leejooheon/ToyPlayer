package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity

import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val githubUseCase: GithubUseCase): BaseViewModel() {
    private val TAG = MainViewModel::class.simpleName

    private val _repositoryResponse =
        mutableStateOf<Resource<List<Entity.Repository>>>(Resource.Default)  // viewModel에서 값에 대한 변경권을 갖고 (private),
    val repositoryResponse = _repositoryResponse // view에서는 State를 활용해 참조만 가능하게 한다.

    private val _lastSearchedOwner = MutableStateFlow("")
    val lastSearchedOwner = _lastSearchedOwner

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

    fun callRepositoryApi(owner: String) {
        _lastSearchedOwner.value = owner
        githubUseCase.getRepository(owner)
            .onEach {
                Log.d(TAG, "result: ${it}")

                _repositoryResponse.value = it
            }
            .launchIn(viewModelScope)
    }
}