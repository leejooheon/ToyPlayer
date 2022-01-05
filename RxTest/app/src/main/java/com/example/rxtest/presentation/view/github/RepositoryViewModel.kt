package com.example.rxtest.presentation.view.github

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.example.rxtest.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RepositoryViewModel @Inject constructor(private val githubUseCase: GithubUseCase) : BaseViewModel() {
    private val TAG = RepositoryViewModel::class.simpleName

    var repositoryLiveData = MutableLiveData<String>()

    private val _repositoryResponse = MutableStateFlow<Resource<List<Entity.Repository>>>(Resource.Default)
    val repositoryResponse = _repositoryResponse

    fun callRepositoryApi(owner: String) {
        githubUseCase.getRepository(owner)
            .onEach { result ->
                Log.d(TAG, "result: ${result}")
                _repositoryResponse.value = result
            }.launchIn(viewModelScope)
    }
}