package com.jooheon.clean_architecture.presentation.view.main

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity

import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val githubUseCase: GithubUseCase): BaseViewModel() {
    override val TAG: String = MainViewModel::class.java.simpleName

    private val _repositoryResponse =
        mutableStateOf<Resource<List<Entity.Repository>>>(Resource.Default)  // viewModel에서 값에 대한 변경권을 갖고 (private),
    val repositoryResponse = _repositoryResponse // view에서는 State를 활용해 참조만 가능하게 한다.

    private val _lastSearchedOwner = MutableStateFlow("")
    val lastSearchedOwner = _lastSearchedOwner

    private val _commitResponse = mutableStateOf<Resource<List<Entity.Commit>>>(Resource.Default)
    val commitResponse = _commitResponse

    private val _branchResponse = mutableStateOf<Resource<List<Entity.Branch>>>(Resource.Default)
    val branchResponse = _branchResponse

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

    fun callRepositoryApi(owner: String) {
        _lastSearchedOwner.value = owner
        githubUseCase.getRepository(owner)
            .onEach {
                Log.d(TAG, "result: ${it}")

                when(it) {
                    is Resource.Success -> {
                        it.value.forEachIndexed { index, repository ->
                            val imgIndex:Int = index % Entity.tempImages.size
                            it.value.get(index).imageUrl = Entity.tempImages.get(imgIndex).imageUrl
                        }
                    }
                }

                _repositoryResponse.value = it
            }
            .launchIn(viewModelScope)
    }

    fun callCommitAndBranchApi(repository: String) {
        githubUseCase.getBranchAndCommit(lastSearchedOwner.value, repository)
            .onEach {
                val branchResponse = it.first
                val commitResponse = it.second
                Log.d(TAG, "result: ${commitResponse is Resource.Loading}")

                _commitResponse.value = commitResponse
                _branchResponse.value = branchResponse

                if (commitResponse is Resource.Success) {
                    Log.d(TAG, "commitResponse: ${commitResponse.value}")
                }

                if (branchResponse is Resource.Success) {
                    Log.d(TAG, "branchResponse: ${branchResponse.value}")
                }
            }.catch {

            }.launchIn(viewModelScope)
    }

    fun onBackPressed() {
        viewModelScope.launch {
            _isDoubleBackPressed.value = false
            delay(2000)
            _isDoubleBackPressed.value = true
        }
    }
}