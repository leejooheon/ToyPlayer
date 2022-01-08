package com.jooheon.clean_architecture.presentation.view.home

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
class HomeViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    private val TAG = HomeViewModel::class.java.simpleName

    private val _repositoryResponse = MutableStateFlow<Resource<List<Entity.Repository>>>(Resource.Default)  // viewModel에서 값에 대한 변경권을 갖고 (private),
    val repositoryResponse = _repositoryResponse // view에서는 State를 활용해 참조만 가능하게 한다.

    private val _commitResponse = MutableStateFlow<Resource<List<Entity.Commit>>>(Resource.Default)
    val commitResponse = _commitResponse

    var onUpdate = mutableStateOf(false)

    private var lastSearchedOwner: String? = null

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
        lastSearchedOwner = owner
        githubUseCase.getRepository(owner)
            .onEach {
                Log.d(TAG, "result: ${it}")
                _repositoryResponse.value = it
                onUpdate.value = !onUpdate.value
            }.launchIn(viewModelScope)
    }
    fun callBranchApi(owner: String, repository: String) {
        githubUseCase.getBranch(owner, repository)
            .onEach {
                if(it is Resource.Success) {
                    Log.d(TAG, "result: ${it.value}")
                    if(!it.value.isEmpty()) {
                        Log.d(TAG, "result - commit: ${it.value.first().commit.sha}, ${it.value.first().commit.url}")
                    }
                }
            }.launchIn(viewModelScope)
    }
    fun callCommitApi(repository: String) {
        lastSearchedOwner?.let { owner ->
            githubUseCase.getCommit(owner, repository)
                .onEach {
                    _commitResponse.value = it
                    onUpdate.value = !onUpdate.value
                }.launchIn(viewModelScope)
        }?.run {
            // TODO: notify to view
        }
    }
}