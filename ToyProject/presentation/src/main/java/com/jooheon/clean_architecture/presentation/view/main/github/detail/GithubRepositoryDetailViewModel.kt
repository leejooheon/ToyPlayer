package com.jooheon.clean_architecture.presentation.view.main.github.detail

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.jooheon.clean_architecture.domain.common.Resource
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.domain.usecase.github.GithubUseCase
import com.jooheon.clean_architecture.features.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GithubRepositoryDetailViewModel @Inject constructor(
    private val githubUseCase: GithubUseCase
): BaseViewModel() {
    override val TAG: String = GithubRepositoryDetailViewModel::class.java.simpleName

    private val _commitResponse = mutableStateOf<List<Entity.Commit>?>(null)
    val commitResponse = _commitResponse

    private val _branchResponse = mutableStateOf<List<Entity.Branch>?>(null)
    val branchResponse = _branchResponse

    fun callCommitAndBranchApi(githubId: String, repository: String) {
        githubUseCase.getBranchAndCommit(githubId, repository)
            .onEach { resources ->
                val branchResponse = resources.first
                val commitResponse = resources.second

                handleResponse(branchResponse)
                handleResponse(commitResponse)

                if (commitResponse is Resource.Success) {
                    Log.d(TAG, "commitResponse: ${commitResponse.value}")
                    _commitResponse.value = commitResponse.value
                }

                if (branchResponse is Resource.Success) {
                    Log.d(TAG, "branchResponse: ${branchResponse.value}")
                    _branchResponse.value = branchResponse.value
                }
            }.launchIn(viewModelScope)
    }
}