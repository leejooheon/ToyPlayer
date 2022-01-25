package com.jooheon.clean_architecture.presentation.view.main.home


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    private val TAG = HomeViewModel::class.java.simpleName

    private val _commitResponse = mutableStateOf<Resource<List<Entity.Commit>>>(Resource.Default)
    val commitResponse = _commitResponse

    private val _branchResponse = mutableStateOf<Resource<List<Entity.Branch>>>(Resource.Default)
    val branchResponse = _branchResponse

    fun callCommitAndBranchApi(owner: String, repository: String) {
        githubUseCase.getBranchAndCommit(owner, repository)
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

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared")
    }
}